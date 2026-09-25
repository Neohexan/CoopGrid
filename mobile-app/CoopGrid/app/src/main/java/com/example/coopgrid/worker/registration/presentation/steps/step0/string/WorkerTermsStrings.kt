package com.example.coopgrid.worker.registration.presentation.steps.step0.string

import com.example.coopgrid.ui.theme.AppLanguage
import kotlinx.serialization.Serializable

@Serializable
data class WorkerTerms(
    val screenCode: String = "",
    val title: String= "Json load nahi ho raha",
    val lastUpdated: String = "",
    val section1Title: String = "",
    val section1Body: String = "",
    val section2Title: String = "",
    val section2Body: String = "",
    val section3Title: String = "",
    val section3Body: String = "",
    val section4Title: String = "",
    val section4Body: String = "",
    val section5Title: String = "",
    val section5Body: String = "",
    val section6Title: String = "",
    val section6Body: String = "",
    val section7Title: String = "",
    val section7Body: String = "",
    val section8Title: String = "",
    val section8Body: String = "",
)
data class TermsStrings(
    val title: String,
    val lastUpdated: String,
    val section1Title: String,
    val section1Body: String,
    val section2Title: String,
    val section2Body: String,
    val section3Title: String,
    val section3Body: String,
    val section4Title: String,
    val section4Body: String,
    val section5Title: String,
    val section5Body: String,
    val section6Title: String,
    val section6Body: String,
    val section7Title: String,
    val section7Body: String,
    val section8Title: String,
    val section8Body: String
)

fun getTermsStrings(language: AppLanguage): TermsStrings {
    return when (language) {
        AppLanguage.HINGLISH -> TermsStrings(
            title = "Terms & Privacy Policy",
            lastUpdated = "Aakhri update: September 2026",
            section1Title = "1. Terms of Service",
            section1Body = "CoopGrid (\"Platform\", \"App\", \"Hum\") par aapka swagat hai. Jab aap CoopGrid mobile application (chahe aap Employer ho, Worker ho, Kisaan ho, ya Trader ho) ko download, install ya use karte hain, toh aap kanooni roop se in sabhi Terms and Conditions se bandh jaate hain. Agar aapko is platform ke niyam manjoor nahi hain, toh aap abhi is application ko uninstall kar dein aur iska upyog band kar dein.\n" +
                    "Yeh niyam hamare platform par hone wale teenon bade kaamon par lagu hote hain: Hyperlocal household services matching, P2P Agriculture Machinery Rental, aur P2P Agriculture Surplus Crop Matching Services.",
            section2Title = "2. ACCOUNT CREATION AUR PROFILE VERIFICATION PROTOCOLS ",
            section2Body = "Mobile Number Verification: Har ek user ko apna active mobile number dena hoga jo OTP (One-Time Password) ke zariye verify kiya jayega. Aapke account se hone wali har activity ke zimmedar aap khud honge.\n" +
                    "Identity Checks:\n" +
                    "Ghar ke Liye (Individual Accounts): User ko apna sarkari pehchaan patra (Aadhaar Card, Voter ID, ya Driving License) aur uski photo upload karni hogi.\n" +
                    "Office/Sanstha ke Liye (Institutional Accounts): Company ya NGO ko apna registered data jaise GSTIN, Udyam Aadhaar, ya Cooperative License details submit karna mandatory hai.\n" +
                    "Cooperative Workers: Workers ko apni registered Labour Cooperative Federation ka membership number aur card photo upload karna hoga.\n" +
                    "Account Block Karne ka Adhikar: Agar koi user farzi document, galat naam, ya galat live location coordinates submit karta hai, toh CoopGrid Admin ke paas bina kisi notice ke us account ko hamesha ke liye block ya delete karne ka poora adhikar hai.\n",
            section3Title = "3. APP KA KAAM AUR \"INFORMATION-ONLY\" DISCLAIMER (SABSE ZAROORI BATT)",
            section3Body = "On-Demand Services: Khaas household services (jaise electrician, plumber, caregiver) ke liye CoopGrid ek real-time geo-routing network ki tarah kaam karta hai, jo customer ko sabse paas ke verified worker se jodta hai.\n" +
                    "Agri-Produce Marketplace Model: Kisaano ke bade scale (bulk crop yield jaise 2000kg tamatar/pyaaz) ke liye CoopGrid sirf ek Information Grid aur Matchmaker ke roop mein kaam karta hai.\n" +
                    "No In-App Payments for Bulk Trade: CoopGrid platform bulk crop trading ke liye koi bhi payment gateway processing, advance deposits, ya financial transaction handle nahi karta hai.\n" +
                    "Offline Transaction Policy: Crop ki quality check, vajan (weight measurement), paise ka len-den (cash ya mandi level settlement), aur logistics (gaadi/transportation ka kharcha) kisaan aur khareeddar (Buyer) aamne-saamne baithkar offline khud tay karenge. Offline deal hone ke baad payment default, crop kharab hone, ya kisi bhi tarah ke fraud ke liye CoopGrid ya uski software team ki koi legal liability (zimmedari) nahi hogi.",
            section4Title = "REGULAR BIDDING AUR EMERGENCY SOS METHOD",
            section4Body = "Bidding System (Express Interest Flow): Regular jobs ke liye, employer request post karega. Aas-pas ke available workers job par apply karenge. Employer worker ki cooperative rating dekhkar kisi ek ko confirm karega. Baaki applicants ke app se wo request automatic hatt jayegi.\n" +
                    "Emergency SOS (Flash Booking): Agar employer emergency mode select karke alert bhejta hai, toh system fast-track par chalega. Yeh system kisi profile review ka wait nahi karega. Jo bhi verified worker sabse pehle Accept button dabayega, kaam immediate usko lock (allocate) ho jayega. Emergency alert aane par worker ke phone par loud siren alarm bajega, chahe phone silent par hi kyun na ho.\n",
            section5Title = "PEER-TO-PEER AGRI-MACHINERY RENTAL LAWS",
            section5Body = "Machinery Maintenance: Jo bhi kisaan apni machinery (tractor, harvester, thresher) Provider App par rent ke liye list karega, wo is baat ki guarantee deta hai ki asset functionally thik hai aur legally registered hai.\n" +
                    "Damage Liability: Rent par lene wala user machinery ko dhyan se chalane ke liye zimmedar hai. Kaam ke dauran agar machinery me koi mechanical breakdown ya damage hota hai, toh uska compensation dono partiyan offline suljhayengi. CoopGrid iske liye koi insurance ya funding provide nahi karta. \n",
            section6Title = "WORKER WELFARE, MINIMUM WAGES, AUR CODE OF CONDUCT",
            section6Body = "Fair Wages Principle: Employer is baat ke liye sahmat hai ki wo worker ko uske kaam ke liye cooperative guidelines ke mutabik sahi aur minimum fair wage rate pay karega. Kisi bhi tarah ka financial exploitation allow nahi kiya jayega.\n" +
                    "Dual Rating Check: Kaam poora hone ke baad worker aur employer dono ek dusre ko rate karenge. Bad behavior, physical safety issues, ya bina wajah payment rokne ki reports aane par user ko platform se hamesha ke liye ban kar diya jayega.",
            section7Title = "LIABILITIES AUR KANOONI SEEMAEIN (LIMITATION OF LIABILITY)",
            section7Body = "As-Is Software Basis: CoopGrid ek \"As-Is\" framework par chalta hai. Hum network blackouts ya internet na hone par 100% live system tracking ki guarantee nahi lete, halanki local room caching features offline kaam ko asan banate hain.\n" +
                    "Zero Injury/Loss Liability: Kisi bhi kaam ke dauran agar kisan ki fasal ka nuksan hota hai, khet me koi accident hota hai, ya worker ko koi physical injury hoti hai, toh CoopGrid team, cooperative partners, aur development unit par koi kanooni case ya financial damage claim nahi kiya ja sakta.",
            section8Title = "NIYAMON MEIN BADLAAV (AMENDMENTS)",
            section8Body = "CoopGrid ke paas in niyam aur sharton ko kabhi bhi badalne ka poora adhikar hai. Naye badlav ke baad agar aap app ka upyog jari rakhte hain, toh iska matlab hai ki aapne badle hue niyam aur sharton ko swikar kar liya hai.",
        )
        else -> TermsStrings(
            title = "Terms & Privacy Policy",
            lastUpdated = "Last updated: September 2026",
            section1Title = "1. Terms of Service",
            section1Body = "Welcome to CoopGrid (\"the Platform\", \"we\", \"us\", \"our\"). By downloading, installing, accessing, or utilizing the CoopGrid mobile applications (including the Consumer/Employer Interface and the Service Provider Interface) or the centralized administration interfaces, you (\"User\", \"Employer\", \"Worker\", \"Farmer\", \"Trader\") explicitly agree to be legally bound by these Terms and Conditions. If you do not agree to the components of this framework, you must immediately terminate usage and uninstall all application software elements.\n" +
                    "These terms govern your usage of hyperlocal on-demand service matching, peer-to-peer agricultural machinery rental discovery, and peer-to-peer agriculture surplus production matching services.",
            section2Title = "2. DUAL INTERFACE IDENTITY AND PROFILE VERIFICATION",
            section2Body = "Account Authenticity: Users must register using an operational mobile number subjected to mandatory multi-factor validation (OTP check). You are entirely accountable for all internal account processes.\n" +
                    "Verification Protocols:\n" +
                    "Household/Individual Accounts: Subjected to government identification audits (Aadhaar Card, Voter Identification, or Driving License verification checks).\n" +
                    "Institutional/Corporate Accounts: Must provide certified regulatory data including GSTIN, Udyam Aadhaar numbers, or valid cooperative federation licenses.\n" +
                    "Cooperative Workers: Must upload authentic, verifiable Labour Cooperative Federation membership badges and registrations.\n" +
                    "Right to Terminate: CoopGrid Administrators reserve absolute authority to freeze, reject, or permanently delete accounts providing forged documents or misleading physical location variables.",
            section3Title = "3. NATURE OF SERVICE DECOUPLING & THE \"INFORMATION-ONLY\" MATRIX",
            section3Body = "Hyperlocal Matching Mechanics: For standard services, CoopGrid functions as an on-demand proximity routing network, connecting buyers with tradespeople or asset owners.\n" +
                    "Agrifood Marketplace Disclaimer: For the trade of bulk agriculture surplus crops and raw yields, CoopGrid operates purely as a matchmaker and decentralized information node.\n" +
                    "Financial Disclaimers: CoopGrid provides visibility, contact linkages, and dynamic geolocation navigation routing. The platform does not process financial transactions, trade escrow systems, or payment settlement clearing systems for bulk crop trades.\n" +
                    "Off-Platform Transactions: All subsequent cash distributions, quality check grading settlements, weight measures, and commercial pricing trades are executed offline directly between the Farmer and the Buyer. CoopGrid bears zero liability for post-discovery discrepancies, quality deterioration, bad weights, or defaults in payment during offline mandates.\n",
            section4Title = "ON-DEMAND ROUTING, BIDDING, AND EMERGENCY SOS MECHANISMS",
            section4Body = "The Bidding / Expression of Interest Flow: For regular postings, employers broadcast a service requirement. Nearby registered workers submit their interest notifications. The final choice rests completely with the Employer based on transparent peer ratings. Workers whose requests are bypassed acknowledge that they hold no claims over the broadcasted job.\n" +
                    "Emergency Flash Bookings (SOS): By utilizing the Emergency SOS system for flash breakdowns, the Platform enforces an atomic allocation script. The system automatically bypasses the employer selection screen and assigns the project to the very first proximal responder within the local database sector. Users accept this automatic system assignment model during urgent requests.\n",
            section5Title = "PEER-TO-PEER AGRI-MACHINERY LEASING PROVISIONS",
            section5Body = "Asset Safety: Farmers listing machinery (tractors, tillers, harvesters) on the Provider App warrant that the machinery is functionally stable, safe, and legally registered.\n" +
                    "Operation Liability: The hirer (Employer App user) is responsible for ensuring secure operation protocols. CoopGrid does not provide hardware damage insurance, mechanical breakdown guarantees, or replacement funding models for asset allocation assets.",
            section6Title = "WORKER WELFARE, MINIMUM WAGES, AND CODE OF CONDUCT",
            section6Body = "Fair Wage Compliance: Employers explicitly covenant to respect regional legal baseline fair wages defined by regional cooperative guidelines and local government notifications. CoopGrid rejects applications deliberately utilizing financial exploitation matrices.\n" +
                    "Mutual Respect Rating: All users are evaluated under a Dual Rating System. Abuse, safety hazards, physical mistreatment, or intentional non-payment patterns reported via the feedback database will cause immediate user banishment from the grid.",
            section7Title = "LIMITATION OF LIABILITY AND INDEMNIFICATION",
            section7Body = "Service Limitations: CoopGrid provides software services on an \"As-Is\" and \"As-Available\" baseline architecture. We do not guarantee uninterrupted local operations in complete network blackouts despite active local room database caching features.\n" +
                    "Liability Boundaries: To the absolute degree permitted by active central and regional legislation, CoopGrid, its regional administrators, cooperative society partners, and software development teams shall not be liable for any physical body injuries, farm asset destruction, financial operational losses, crop spoilage, or cargo movement structural complications arising from matches made on the platform.",
            section8Title = "AMENDMENTS AND SYSTEM UPDATES",
            section8Body = "We reserve the absolute right to modify, adapt, or completely overhaul these terms at any checkpoint. Continued operation of the platform following dynamic text changes indicates automated user validation of the newly updated Terms and Conditions structure.",
        )
    }
}