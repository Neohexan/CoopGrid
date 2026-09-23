package com.example.coopgrid.employer.registration.presentation.steps.step3_1.model.wholesaler

import com.example.coopgrid.ui.theme.AppLanguage

enum class TradeType {
    WHOLESALER,
    COMMISSION_AGENT,
    DISTRIBUTOR,
    SUPPLIER;

    fun getDisplayName(language: AppLanguage): String {
        return when (this) {
            WHOLESALER -> if (language == AppLanguage.HINGLISH) "Wholesaler (Maha-Vikreta)" else "Wholesaler"
            COMMISSION_AGENT -> if (language == AppLanguage.HINGLISH) "Arhatiya / Commission Agent" else "Commission Agent / Mandi Agent"
            DISTRIBUTOR -> if (language == AppLanguage.HINGLISH) "Stockist / Distributor" else "Distributor"
            SUPPLIER -> if (language == AppLanguage.HINGLISH) "Trader / Supplier" else "Trader & Supplier"
        }
    }
}