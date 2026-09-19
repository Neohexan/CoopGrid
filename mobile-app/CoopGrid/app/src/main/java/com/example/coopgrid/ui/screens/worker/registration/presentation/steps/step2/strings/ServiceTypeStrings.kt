package com.example.coopgrid.ui.screens.worker.registration.presentation.steps.step2.strings

import com.example.coopgrid.ui.theme.AppLanguage


data class ServiceTypeOptionUIModel(
    val title: String,
    val description: String,
    val iconResOrName: String
)

data class ServiceTypeStrings(
    val screenTitle: String,
    val screenSubtitle: String,
    val personalSkillOption: ServiceTypeOptionUIModel,
    val machineryRentalOption: ServiceTypeOptionUIModel,
    val agriSupplyOption: ServiceTypeOptionUIModel,
    val continueButton: String,
    val selectAtLeastOneError: String
)

val EnglishServiceTypeStrings = ServiceTypeStrings(
    screenTitle = "Select What You Offer",
    screenSubtitle = "Select your main service type. You can add more services from profile later.",
    personalSkillOption = ServiceTypeOptionUIModel(
        title = "Individual / Skilled Work",
        description = "Provide manual or skilled labour services like Electrician, Plumber, Cook, Labour, etc.",
        iconResOrName = "ic_person_work"
    ),
    machineryRentalOption = ServiceTypeOptionUIModel(
        title = "Machinery & Equipment Rental",
        description = "Rent out machines or vehicles with/without drivers like Tractor, Thresher, Harvester, Borewell, JCB.",
        iconResOrName = "ic_tractor"
    ),
    agriSupplyOption = ServiceTypeOptionUIModel(
        title = "Agri Supplies & Produce",
        description = "Sell or supply agricultural goods like Seeds, Organic Fertilizers, Saplings, or Raw Farm Produce.",
        iconResOrName = "ic_agri_goods"
    ),
    continueButton = "Continue",
    selectAtLeastOneError = "Please select at least one service type to proceed"
)

val HinglishServiceTypeStrings = ServiceTypeStrings(
    screenTitle = "Aap Kya Service Dena Chahte Hain?",
    screenSubtitle = "Sahi category chunein. Aap bad me profile se dusri services bhi add kar sakte hain.",
    personalSkillOption = ServiceTypeOptionUIModel(
        title = "Individual / Mistry Kaam (Skills)",
        description = "Aap khud ja kar kaam karenge (Jaise Electrician, Plumber, Khana Banane Wale, Kheti Majdoor).",
        iconResOrName = "ic_person_work"
    ),
    machineryRentalOption = ServiceTypeOptionUIModel(
        title = "Machinery & Vehicle Kiraye Par",
        description = "Tractor, Thresher, JCB, Water Pump ya koi bhi Kheti / Construction Machine kiraye par dena.",
        iconResOrName = "ic_tractor"
    ),
    agriSupplyOption = ServiceTypeOptionUIModel(
        title = "Kheti Ka Saman & Fasal (Agri Goods)",
        description = "Khet ke Beej, Khad, Paudhe ya Khet ki Fasal / Produce bechna ya supply karna.",
        iconResOrName = "ic_agri_goods"
    ),
    continueButton = "Aage Badhein",
    selectAtLeastOneError = "Aage badhne ke liye kam se kam ek option chunein"
)

fun getServiceTypeStrings(language: AppLanguage): ServiceTypeStrings {
    return when (language) {
        AppLanguage.HINGLISH -> HinglishServiceTypeStrings
        AppLanguage.ENGLISH -> EnglishServiceTypeStrings
    }
}