package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.deta.skills


val TechnicalCategory = SkillCategoryModel(
    id = "cat_tech",
    specificSkills = listOf(
        SpecificSkillModel(
            id = "sk_electrician",
            subSkills = listOf(
                SubSkillModel("sub_elec_wiring"),
                SubSkillModel("sub_elec_appliance"),
                SubSkillModel("sub_elec_ac"),
                SubSkillModel("sub_elec_inv_solar"),
                SubSkillModel("sub_elec_mcb_panel"),
                SubSkillModel("sub_elec_light_decor"),
                SubSkillModel("sub_elec_industrial"),
                SubSkillModel("sub_elec_earthing")
            )
        ),
        SpecificSkillModel(
            id = "sk_plumber",
            subSkills = listOf(
                SubSkillModel("sub_plumb_pipe"),
                SubSkillModel("sub_plumb_sanitary"),
                SubSkillModel("sub_plumb_geyser"),
                SubSkillModel("sub_plumb_water_tank")
            )
        )
    )
)