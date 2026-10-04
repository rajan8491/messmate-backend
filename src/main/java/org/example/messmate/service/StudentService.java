package org.example.messmate.service;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.example.messmate.dto.AddRatingRequest;
import org.example.messmate.dto.ExtraPurchaseInfo;
import org.example.messmate.dto.ExtraPurchaseDto;
import org.example.messmate.dto.extraAnalysisDto.*;
import org.example.messmate.dto.otpDto.OtpSendRequestDto;
import org.example.messmate.dto.otpDto.OtpSendResponseDto;
import org.example.messmate.dto.studentdto.RemoveStudentVerifyRequestDto;
import org.example.messmate.dto.studentdto.StudentResponseDto;
import org.example.messmate.entity.*;
import org.example.messmate.enums.GroupBy;
import org.example.messmate.enums.OtpPurpose;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.exception.UserNotFoundException;
import org.example.messmate.mapper.StudentMapper;
import org.example.messmate.repository.*;
import org.example.messmate.repository.menuRepository.ExtraItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final HostelRepository hostelRepository;
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final StudentExtraRepository studentExtraRepository;
    private final ExtraItemRepository extraItemRepository;
    private final RatingService ratingService;

    public StudentService(
            StudentRepository studentRepository,
            HostelRepository hostelRepository,
            OtpService otpService,
            UserRepository userRepository,
            StudentExtraRepository studentExtraRepository,
            ExtraItemRepository extraItemRepository,
            RatingService ratingService
    ) {
        this.studentRepository = studentRepository;
        this.hostelRepository = hostelRepository;
        this.otpService = otpService;
        this.userRepository = userRepository;
        this.studentExtraRepository = studentExtraRepository;
        this.extraItemRepository = extraItemRepository;
        this.ratingService = ratingService;
    }

    public StudentResponseDto getStudentProfile(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return StudentMapper.toDto(user.getStudent());
    }

    @Transactional
    public void addStudentExtra(@Valid ExtraPurchaseDto extraPurchaseDto, String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Set<Long> incomingExtraIds = new HashSet<>();
        extraPurchaseDto.getItems()
                .stream()
                .map(ExtraPurchaseInfo::getItemId)
                .filter(Objects::nonNull)
                .forEach(incomingExtraIds::add);

        Map<Long, ExtraItem> extraItems =
                extraItemRepository
                        .findAllByIdIn(incomingExtraIds)
                        .stream()
                        .collect(Collectors.toMap(
                                ExtraItem::getId,
                                Function.identity()
                        ));

        List<StudentExtra> studentExtras = new ArrayList<>();
        for(ExtraPurchaseInfo extras : extraPurchaseDto.getItems()) {
            StudentExtra studentExtra = new StudentExtra();
            studentExtra.setStudent(user.getStudent());
            studentExtra.setDate(extraPurchaseDto.getDate());

            ExtraItem extraItem = extraItems.get(extras.getItemId());

            studentExtra.setExtra(extraItem);
            studentExtra.setPrice(extraItem.getPrice());
            studentExtra.setQuantity(extras.getQty());

            studentExtras.add(studentExtra);
        }

        studentExtraRepository.saveAll(studentExtras);
    }

    public ExtraAnalysisResponse analyseExtraPurchase(
            String username,
            LocalDate from,
            LocalDate to,
            String groupBy
    ) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Date range is required");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }

        if (groupBy == null) {
            throw new IllegalArgumentException("Group by is required");
        }

        GroupBy groupByEnum = GroupBy.valueOf(groupBy.toUpperCase());

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        List<StudentExtra> studentExtras =
                studentExtraRepository.findForAnalysis(
                        user.getStudent(),
                        from,
                        to
                );

        // --------------------------------------------------
        // 1. General statistics
        // --------------------------------------------------

        BigDecimal total = BigDecimal.ZERO;
        long count = 0;

        // Money spent per extra
        Map<String, BigDecimal> spendingByItem = new LinkedHashMap<>();

        // Quantity purchased per extra
        Map<String, Long> quantityByItem = new LinkedHashMap<>();

        // Spending grouped by date/week/month
        Map<String, BigDecimal> spendingTrend = new TreeMap<>();

        for (StudentExtra extra : studentExtras) {

            BigDecimal price = extra.getPrice();
            long quantity = extra.getQuantity();

            BigDecimal purchaseTotal =
                    price.multiply(BigDecimal.valueOf(quantity));

            // Total spending
            total = total.add(purchaseTotal);

            // Total quantity
            count += quantity;

            String itemName = extra.getExtra().getName();

            // --------------------------------------------------
            // Pie: spending per item
            // --------------------------------------------------

            spendingByItem.merge(
                    itemName,
                    purchaseTotal,
                    BigDecimal::add
            );

            // --------------------------------------------------
            // Bar chart: quantity per item
            // --------------------------------------------------

            quantityByItem.merge(
                    itemName,
                    quantity,
                    Long::sum
            );

            // --------------------------------------------------
            // Trend
            // --------------------------------------------------

            String trendKey = getTrendKey(
                    extra.getDate(),
                    groupByEnum
            );

            spendingTrend.merge(
                    trendKey,
                    purchaseTotal,
                    BigDecimal::add
            );
        }

        // --------------------------------------------------
        // 2. Average spending per day
        // --------------------------------------------------

        long numberOfDays =
                ChronoUnit.DAYS.between(from, to) + 1;

        BigDecimal avgPerDay =
                numberOfDays == 0
                        ? BigDecimal.ZERO
                        : total.divide(
                        BigDecimal.valueOf(numberOfDays),
                        2,
                        RoundingMode.HALF_UP
                );

        // --------------------------------------------------
        // 3. Pie data
        // --------------------------------------------------

        List<PieStat> pie = spendingByItem.entrySet()
                .stream()
                .map(entry ->
                        new PieStat(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();

        // --------------------------------------------------
        // 4. Item-wise quantity
        // --------------------------------------------------

        List<ItemStat> items = quantityByItem.entrySet()
                .stream()
                .map(entry ->
                        new ItemStat(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();

        // --------------------------------------------------
        // 5. Trend data
        // --------------------------------------------------

        List<TrendStat> trendStats = spendingTrend.entrySet()
                .stream()
                .map(entry ->
                        new TrendStat(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .toList();

        // --------------------------------------------------
        // 6. Build response
        // --------------------------------------------------

        GeneralStats generalStats = new GeneralStats(
                total,
                count,
                avgPerDay,
                pie,
                items
        );

        return new ExtraAnalysisResponse(
                generalStats,
                trendStats
        );

    }

    private String getTrendKey(
            LocalDate date,
            GroupBy groupBy
    ) {
        return switch (groupBy) {

            case DAILY ->
                    date.toString();

            case WEEKLY -> {
                LocalDate weekStart =
                        date.with(
                                java.time.temporal.TemporalAdjusters
                                        .previousOrSame(DayOfWeek.MONDAY)
                        );

                yield weekStart.toString();
            }

            case MONTHLY ->
                    date.getYear()
                            + "-"
                            + String.format(
                            "%02d",
                            date.getMonthValue()
                    );
        };
    }

    public void addRating(
            String username,
            AddRatingRequest ratingDto
    ) throws BadRequestException {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(UserNotFoundException::new);

        Student student = user.getStudent();

        ratingService.rateItem(student, ratingDto);
    }



    public void changeHostel(String username, Long hostelId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        Student student = user.getStudent();

        if(student == null) throw new UserNotFoundException("Student not found");

        Hostel hostel = hostelRepository.findById(hostelId).orElse(null);

        if(hostel == null) throw new ResourceNotFoundException("Hostel not found");

        student.setHostel(hostel);
    }

    public List<StudentResponseDto> getAllStudentsByHostelId(
            Long hostelId
    ) {
        List<Student> students =
                studentRepository.findAllByHostelId(hostelId);

        List<StudentResponseDto> studentsDto = new ArrayList<>();
        for (Student student : students) {
            studentsDto.add(
                    StudentMapper.toDto(student)
            );
        }
        return studentsDto;
    }

    public OtpSendResponseDto sendStudentRemoveOtp(
            OtpSendRequestDto otpSendRequestDto
    ) {
        String identifier =
                otpSendRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.requestOtp(
                identifier,
                OtpPurpose.REMOVE_STUDENT,
                otpSendRequestDto.getChannel()
        );

        OtpSendResponseDto responseDto =
                new OtpSendResponseDto();

        responseDto.setIdentifier(identifier);
        return responseDto;
    }

    @Transactional
    public void removeStudentsByHostelId(
            Long id,
            RemoveStudentVerifyRequestDto removeStudentVerifyRequestDto
    ) {
        String identifier =
                removeStudentVerifyRequestDto
                        .getIdentifier()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        otpService.verifyOtp(
                identifier,
                OtpPurpose.REMOVE_STUDENT,
                removeStudentVerifyRequestDto.getOtp()
        );

        List<String> studentIdentifiers =
                removeStudentVerifyRequestDto
                        .getStudentIdentifiers();
        

        userRepository.deleteAllByUsernameIn(studentIdentifiers);

    }
}
