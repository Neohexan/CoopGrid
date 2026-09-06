package com.example.coopgrid.ui.screens.worker.dashboard.strings

import com.example.coopgrid.ui.screens.worker.dashboard.HomeBannerCoustomItem


import androidx.compose.ui.graphics.Color

val dummyServerBannersData = listOf(
    HomeBannerCoustomItem(
        id = "banner_1",
        title = "🚀 SIH 2026 Hackathon",
        description = "Karim City College mein Smart India Hackathon internal round shuru! Apni team register karein.",
        badgeText = "KCC EVENT",
        actionText = "Register Team",
        primaryColor = Color(0xFF6200EE) // Premium Royal Purple
    ),
    HomeBannerCoustomItem(
        id = "banner_2",
        title = "⚡ Diwale Bonus Offer!",
        description = "Pehli 5 jobs complete karein aur paayein ₹500 ka instant extra cash bonus direct wallet me.",
        badgeText = "OFFER",
        actionText = "Claim Bonus",
        primaryColor = Color(0xFFD32F2F) // Action Red / Orange
    ),
    HomeBannerCoustomItem(
        id = "banner_3",
        title = "🏗️ Top Builder Hiring!",
        description = "Noida Sector 62 site ke liye skilled Plumbers aur Electricians ki turant jarurat hai. High Pay!",
        badgeText = "URGENT",
        actionText = "Apply Now",
        primaryColor = Color(0xFF00897B) // Modern Vibrant Teal
    ),
    HomeBannerCoustomItem(
        id = "banner_4",
        title = "🛡️ 100% Guaranteed Payment",
        description = "CoopGrid Escrow Protection ke sath har kaam ka paisa direct bank account me, bina kisi delay ke.",
        badgeText = "TRUSTED",
        actionText = "Know More",
        primaryColor = Color(0xFF1E88E5) // Deep Electric Blue
    )
)