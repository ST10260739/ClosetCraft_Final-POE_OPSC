Closet Craft - Mobile Fashion App
 Overview
Closet Craft is a modern, convenient e-commerce mobile application designed for fashion-forward individuals. It offers a wide range of stylish, modern-vintage clothing and accessories for both men and women. The app is designed for convinience, no need to visit physical stores, allowing users to shop, compare, and discover new styles anytime, anywhere.

the goal is for users to have a convenient online shopping experience and simplifying the discovery and purchase of high-quality, unique clothing.

Target Audience

Individuals aged 18–45.
Shoppers who appreciate modern-vintage fashion.
Users seeking a straightforward and reliable online shopping experience.


Key Features



Personalized Shopping Browse clothing categories specifically tailored for both men and women.


Advanced Product Discovery Search and filter products by size, color, price.


Secure Shopping Experience Integrated payment system with multiple payment options.

Wishlist: Save items for later with a simple heart icon.

Dark Mode Support: A toggleable interface allowing users to switch between light and dark themes for reduced eye strain and personalized comfort.


Responsive Design Optimized for both mobile and tablet devices.

Offline Mode Provides limited offline access for Login and Settings, allowing users to authenticate and manage preferences even without internet connectivity.


Single Sign-On (SSO) Simplified and secure authentication through an SSO provider, enabling faster and more reliable access.


Real-Time Notifications Push-notifications feature enabling instant alerts on new arrivals, promotions, and many more


Multi-Language Support (English & Afrikaans) Users can select their preferred language. Afrikaans has been added as one of the supported languages due to its widespread use.


Technical Implementation Architecture & Technologies


Frontend: React Native / Android (Kotlin) with Material Design
Backend Services: Firebase Ecosystem
Authentication: Firebase Authentication API + SSO provider
Database: Firebase Firestore (NoSQL) supporting real-time data sync
Storage: Firebase Storage for product images
Payments: Stripe API integration via Firebase Cloud Functions
Offline Support: Firebase persistence + local secure storage for key areas
Notifications: Firebase Cloud Messaging (FCM)
External API: Fake Store REST API for product data



API Integration:
The app integrates with the Fake Store REST API to fetch and display clothing products. This provides:
Realistic product data for development and testing
Structured product information including images, prices, and categories


A demo inventory to showcase app functionality


Data Structure
Collections include:
users – User profiles, preferences, and language settings

products – Clothing items with detailed attributes

carts – Shopping cart management

Unique Value Proposition
Closet Craft differentiates itself through:

Modern-Vintage Fashion: A unique blend of contemporary and classic styles

Quality Focus: High-quality clothing at affordable prices

Enhanced User Experience: Intuitive interface inspired by industry leaders, supported by multilingual support, SSO, notifications, and offline capability

Development Status
Currently in development with the following milestones completed or in progress:

Project Setup Complete

Core Features Implementation

Payment Integration

Push-Notifications, SSO, Multi-language (including Afrikaans), and Offline Mode added
Automated Testing using GitHub Actions

Production Launch on Google Play Store

Development Team:
Promise Lushaba (ST10328803)
Tshiamo Maredi (ST10260739)
Nompilo Mnisi (ST10265120)

Youtube Link: https://youtu.be/zhIzWjVCt8A
