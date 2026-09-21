package com.example.coopgrid.worker.registration.presentation.steps.step21.deta.skills


val DomesticCategory = SkillCategoryModel(
    id = "cat_domestic",
    specificSkills = listOf(
        SpecificSkillModel(
            id = "sk_cook",
            subSkills = listOf(
                SubSkillModel("sub_cook_veg"),
                SubSkillModel("sub_cook_nonveg"),
                SubSkillModel("sub_cook_south_indian"),
                SubSkillModel("sub_cook_chinese"),
                SubSkillModel("sub_cook_sweets")
            )
        ),
        SpecificSkillModel(
            id = "sk_cleaner",
            subSkills = listOf(
                SubSkillModel("sub_clean_deep"),
                SubSkillModel("sub_clean_utensils"),
                SubSkillModel("sub_clean_dusting")
            )
        )
    )
)