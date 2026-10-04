package org.example.messmate.service;

import org.apache.coyote.BadRequestException;
import org.example.messmate.dto.AddRatingRequest;
import org.example.messmate.entity.ItemRating;
import org.example.messmate.entity.Student;
import org.example.messmate.enums.ItemType;
import org.example.messmate.exception.AlreadyRatedException;
import org.example.messmate.exception.ResourceNotFoundException;
import org.example.messmate.repository.ItemRatingRepository;
import org.example.messmate.repository.menuRepository.DietItemRepository;
import org.example.messmate.repository.menuRepository.ExtraItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RatingService {
    private static final Map<ItemType, Map<Integer, Set<String>>> ALLOWED_TAGS =
            Map.of(
                    ItemType.DIET,
                    Map.of(
                            1, Set.of(
                                    "Foreign Object",
                                    "Oily",
                                    "Undercooked",
                                    "Burnt",
                                    "Unhygienic",
                                    "Unhealthy",
                                    "Inedible",
                                    "Smelling"
                            ),

                            2, Set.of(
                                    "Watery",
                                    "Chewy",
                                    "Tasteless",
                                    "Too Spicy",
                                    "Served Cold",
                                    "Poor Quality"
                            ),

                            3, Set.of(
                                    "Average Taste",
                                    "Fine",
                                    "Healthy but Plain",
                                    "Standard Quality",
                                    "Decent Hygiene"
                            ),

                            4, Set.of(
                                    "Tasty & Fresh",
                                    "Very Hygienic",
                                    "Healthy Option",
                                    "Perfect Balance",
                                    "Nice & Hot",
                                    "Thick & Fresh"
                            ),

                            5, Set.of(
                                    "Delicious!",
                                    "Super Clean!",
                                    "Highly Nutritious",
                                    "Home-like Taste",
                                    "Perfect Spices",
                                    "Loved It!"
                            )
                    ),

                    ItemType.EXTRA,
                    Map.of(
                            1, Set.of(
                                    "Unclean Prep",
                                    "Overpriced",
                                    "Tiny Portion",
                                    "Stale/Not Fresh",
                                    "Too Greasy",
                                    "Horrible Taste"
                            ),

                            2, Set.of(
                                    "Lacks Flavor",
                                    "Soggy or Limp",
                                    "Disappointing Size",
                                    "Too Sweet",
                                    "Dry or Chewy",
                                    "Too Artificial"
                            ),

                            3, Set.of(
                                    "Okay Flavor",
                                    "Decent Portion",
                                    "Clean Enough",
                                    "Worth a Try",
                                    "Satisfied Cravings"
                            ),

                            4, Set.of(
                                    "Value for Money",
                                    "Great Taste",
                                    "Good Quantity",
                                    "Fresh & Clean",
                                    "Crispy & Hot",
                                    "Rich Flavor"
                            ),

                            5, Set.of(
                                    "Worth Every Penny!",
                                    "Absolute Fire",
                                    "Premium Quality",
                                    "Super Hygienic",
                                    "Perfect Portion"
                            )
                    )
            );


    private static final int MAX_TAGS = 10;

    private final ItemRatingRepository itemRatingRepository;
    private final DietItemRepository dietItemRepository;
    private final ExtraItemRepository extraItemRepository;

    public RatingService(ItemRatingRepository itemRatingRepository, DietItemRepository dietItemRepository, ExtraItemRepository extraItemRepository) {
        this.itemRatingRepository = itemRatingRepository;
        this.dietItemRepository = dietItemRepository;
        this.extraItemRepository = extraItemRepository;
    }

    @Transactional(rollbackFor = java.lang.Exception.class)
    public void rateItem(
            Student student,
            AddRatingRequest addRatingDto
    ) throws BadRequestException {

        LocalDate today = LocalDate.now();

        boolean alreadyRated =
                itemRatingRepository
                        .existsByStudentIdAndItemIdAndItemTypeAndMealAndRatedDate(
                                student.getId(),
                                addRatingDto.getItemId(),
                                addRatingDto.getItemType(),
                                addRatingDto.getMeal(),
                                today
                        );

        if (alreadyRated) {
            throw new AlreadyRatedException(
                    "You have already rated this item for this meal"
            );
        }

        validateItem(addRatingDto.getItemId(), addRatingDto.getItemType());

        validateTags(
                addRatingDto.getItemType(),
                addRatingDto.getRating(),
                addRatingDto.getTags()
        );

        ItemRating rating = new ItemRating();

        rating.setStudent(student);
        rating.setItemId(addRatingDto.getItemId());
        rating.setItemType(addRatingDto.getItemType());
        rating.setMeal(addRatingDto.getMeal());
        rating.setRating(addRatingDto.getRating());

        if (addRatingDto.getTags() != null) {
            rating.setTags(
                    addRatingDto.getTags().toArray(new String[0])
            );
        }

        rating.setSuggestion(
                addRatingDto.getSuggestion()
        );

        rating.setRatedDate(today);

        itemRatingRepository.save(rating);
    }


    private void validateItem(
            Long itemId,
            ItemType itemType
    ) {

        if (itemType == ItemType.DIET) {

            if (!dietItemRepository.existsById(itemId)) {
                throw new ResourceNotFoundException(
                        "Diet item not found"
                );
            }

        } else {

            if (!extraItemRepository.existsById(itemId)) {
                throw new ResourceNotFoundException(
                        "Extra item not found"
                );
            }
        }
    }

    private void validateTags(
            ItemType itemType,
            Integer rating,
            List<String> tags
    ) throws BadRequestException {

        if (tags == null || tags.isEmpty()) {
            return;
        }

        if (tags.size() > MAX_TAGS) {
            throw new BadRequestException(
                    "You can select a maximum of " + MAX_TAGS + " tags"
            );
        }

        Set<String> selectedTags = new HashSet<>(tags);

        if (selectedTags.size() != tags.size()) {
            throw new BadRequestException(
                    "Duplicate tags are not allowed"
            );
        }

        Set<String> allowedTags =
                ALLOWED_TAGS
                        .getOrDefault(itemType, Map.of())
                        .get(rating);

        if (allowedTags == null) {
            throw new BadRequestException(
                    "Invalid rating"
            );
        }

        for (String tag : selectedTags) {
            if (!allowedTags.contains(tag)) {
                throw new BadRequestException(
                        "Invalid tag: " + tag
                );
            }
        }
    }
}
