

# CouponChest - Android App 📱💰

Welcome to CouponChest! This Android application is designed to help users manage and organize their coupons and promotional codes efficiently.

## Features

📋 **Coupon Storage**: Easily add and organize your coupons and promotional codes.

📷 **Image Upload**: Upload images of your coupons or promotional materials.

🔍 **Text Recognition**: Use ML Kit to recognize and extract promo codes from images.

📍 **Location Services**: View your current location on a map.

🗺️ **OpenStreetMap Integration**: Utilizes OpenStreetMap for displaying location information.

## System Goals

The main objectives of this system are:

1. **Efficient Coupon Management**: Easily add, edit, and delete coupons and promo codes.

2. **Image-based Code Extraction**: Utilize image recognition to automatically extract promo codes from images.

3. **Location Awareness**: Provide users with their current location information.

## Technologies Used

This application is built using the following technologies and libraries:

- **Kotlin**: Primary programming language for Android development.
- **Android Jetpack**: 
  - ViewModel
  - LiveData
  - Room Database
  - Navigation Component
- **Coroutines**: For managing background tasks and asynchronous operations.
- **ML Kit**: For text recognition in images.
- **Glide**: For efficient image loading and caching.
- **OpenStreetMap**: For map integration and location display.
- **ViewBinding**: For efficient view access.

## Getting Started

To get started with CouponChest:

1. Clone this repository:

    ```bash
    git clone https://github.com/yourusername/CouponChest.git
    ```

2. Open the project in Android Studio.

3. Build and run the application on your Android device or emulator.

## Project Structure

The project follows a typical MVVM (Model-View-ViewModel) architecture:

- `data`: Contains the data layer (models, database, and repository).
- `ui`: Contains the user interface layer (fragments and adapters).
- `viewModel`: Contains the ViewModel classes.

## Contributing

Contributions are welcome! If you'd like to contribute to this project, please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature-name`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add some feature'`).
5. Push to the branch (`git push origin feature/your-feature-name`).
6. Create a new Pull Request.

## License

This project is licensed under the [MIT License](LICENSE).

---

Enjoy managing your coupons and promo codes with CouponChest! If you have any questions or suggestions, feel free to open an issue or reach out to the project maintainers.
