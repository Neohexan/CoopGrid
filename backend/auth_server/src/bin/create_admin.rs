use rand::Rng;
use std::io::{self, Write};
// Apne Cargo.toml ke package name se yahan import karein (e.g., use auth_server::storage::admin_store::{AdminStore, AdminRecord};)
use auth_server::storage::admin_storage::{AdminRecord, AdminStore};
use auth_server::utils::password_argon2_hashing::hash_password_argon2;
fn main() {
    // .env file load karna taaki deletion secret mil sake
    dotenvy::dotenv().ok();

    println!("=== Enterprise Admin Management CLI ===");
    println!("1. Add New Admin");
    println!("2. Delete Admin");
    println!("3. List All Admins");
    print!("Choose an option (1, 2, or 3): ");
    io::stdout().flush().unwrap();

    let mut choice = String::new();
    io::stdin().read_line(&mut choice).unwrap();
    let choice = choice.trim();

    match choice {
        "1" => handle_add_admin(),
        "2" => handle_delete_admin(),
        "3" => handle_list_admins(),
        _ => println!("[-] Invalid choice! Please run the command again."),
    }
}

// 1. Add Admin Function
fn handle_add_admin() {
    println!("\n--- Add New Admin ---");

    let mut name = String::new();
    let mut dob = String::new();
    let mut state = String::new();
    let mut district = String::new();
    let mut block = String::new();

    print!("Enter Admin Name: ");
    io::stdout().flush().unwrap();
    io::stdin().read_line(&mut name).unwrap();
    let name = name.trim();

    print!("Enter Date of Birth (YYYY-MM-DD): ");
    io::stdout().flush().unwrap();
    io::stdin().read_line(&mut dob).unwrap();
    let dob = dob.trim();

    print!("Enter State: ");
    io::stdout().flush().unwrap();
    io::stdin().read_line(&mut state).unwrap();
    let state = state.trim();

    print!("Enter District: ");
    io::stdout().flush().unwrap();
    io::stdin().read_line(&mut district).unwrap();
    let district = district.trim();

    print!("Enter Block (Press Enter to skip): ");
    io::stdout().flush().unwrap();
    io::stdin().read_line(&mut block).unwrap();
    let block_input = block.trim();
    let block = if block_input.is_empty() {
        None
    } else {
        Some(block_input.to_string())
    };

    // Clean name
    let clean_name: String = name
        .chars()
        .filter(|c| c.is_alphabetic())
        .collect::<String>()
        .to_lowercase();

    // DOB Sum (Year + Month + Day)
    let dob_parts: Vec<&str> = dob.split('-').collect();
    let dob_sum = if dob_parts.len() == 3 {
        let y: i32 = dob_parts[0].parse().unwrap_or(2000);
        let m: i32 = dob_parts[1].parse().unwrap_or(1);
        let d: i32 = dob_parts[2].parse().unwrap_or(1);
        y + m + d
    } else {
        2020
    };

    // Compact Admin ID (Name + DOB Sum)
    let admin_id = format!("{}{}", clean_name, dob_sum);

    let temp_password = generate_secure_temp_password();
    let password_hash =
        hash_password_argon2(&temp_password).expect("Critical: Failed to hash admin password");
    // ✨ Updated method call matching admin_store.rs
    let mut store = AdminStore::load_from_disk();

    if store.admins.iter().any(|a| a.admin_id == admin_id) {
        println!("\n[-] Error: Admin with ID '{}' already exists!", admin_id);
        return;
    }

    let new_admin = AdminRecord {
        uuid: None,
        admin_id: admin_id.clone(),
        password_hash,
        state: state.to_string(),
        district: district.to_string(),
        block,
        must_change_password: true,
    };

    store.admins.push(new_admin);

    // ✨ Updated save method call matching admin_store.rs
    match store.save_to_disk() {
        Ok(_) => {
            println!("\n[+] Professional Admin successfully created!");
            println!("--------------------------------------------------");
            println!("Admin ID (Username) : {}", admin_id);
            println!("Temporary Password  : {}", temp_password);
            println!("State / District    : {}, {}", state, district);
            println!("Status              : Must change password on first login.");
            println!("--------------------------------------------------");
        }
        Err(e) => {
            println!("\n[-] Failed to save admin store: {}", e);
        }
    }
}

// 2. Delete Admin Function (Protected by .env password)
fn handle_delete_admin() {
    println!("\n--- Delete Admin ---");

    print!("Enter Admin ID to delete: ");
    io::stdout().flush().unwrap();
    let mut admin_id = String::new();
    io::stdin().read_line(&mut admin_id).unwrap();
    let admin_id = admin_id.trim();

    print!("Enter Master Deletion Password (.env secret): ");
    io::stdout().flush().unwrap();
    let mut input_password = String::new();
    io::stdin().read_line(&mut input_password).unwrap();
    let input_password = input_password.trim();

    // .env se secret fetch karna
    let expected_secret = std::env::var("ADMIN_DELETE_SECRET").unwrap_or_else(|_| "".to_string());

    if expected_secret.is_empty() || input_password != expected_secret {
        println!("\n[-] Access Denied: Incorrect deletion password or secret not set in .env!");
        return;
    }

    let mut store = AdminStore::load_from_disk();
    let initial_len = store.admins.len();

    // Admin ko filter karke remove karna
    store.admins.retain(|a| a.admin_id != admin_id);

    if store.admins.len() < initial_len {
        match store.save_to_disk() {
            Ok(_) => println!(
                "\n[+] Success: Admin '{}' has been permanently deleted.",
                admin_id
            ),
            Err(e) => println!("\n[-] Error saving store after deletion: {}", e),
        }
    } else {
        println!("\n[-] Error: Admin ID '{}' not found in store.", admin_id);
    }
}

// 3. List Admins Function (Basic Stats)
fn handle_list_admins() {
    println!("\n--- Registered Admins Overview ---");
    let store = AdminStore::load_from_disk();

    println!("Total Active Admins: {}\n", store.admins.len());

    if store.admins.is_empty() {
        println!("No admins registered yet.");
        return;
    }

    for (index, admin) in store.admins.iter().enumerate() {
        println!("{}. Admin ID: {}", index + 1, admin.admin_id);
        println!(
            "  Location: {}, {} (Block: {:?})",
            admin.state, admin.district, admin.block
        );
        println!(
            "  UUID Bound: {}",
            if admin.uuid.is_some() {
                "Yes"
            } else {
                "No (Pending First Login)"
            }
        );
        println!("  Must Change Password: {}", admin.must_change_password);
        println!("--------------------------------------------------");
    }
}

// Helpers
fn generate_secure_temp_password() -> String {
    const CHARSET: &[u8] = b"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$!";
    let mut rng = rand::thread_rng();
    (0..12)
        .map(|_| {
            let idx = rng.gen_range(0..CHARSET.len());
            CHARSET[idx] as char
        })
        .collect()
}

// run commond: cargo run --bin create_admin
