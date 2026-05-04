# Streamflix Frontend Setup Guide

## Overview
This React frontend connects to the Spring Boot backend streaming system. It provides a complete UI for managing movies, shows, genres, reviews, profiles, accounts, plans, and subscriptions.

## Prerequisites
- Node.js installed
- Backend server running on `http://localhost:8080`

## Installation

1. Navigate to the frontend directory:
```bash
cd Frontend/StreamflixUI
```

2. Install dependencies (if not already installed):
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173` (or another port if 5173 is busy).

## Backend Connection

The frontend is configured to connect to the backend at `http://localhost:8080`. This is set in `src/services/api.ts`.

If your backend runs on a different port, update the `API_BASE_URL` constant in `src/services/api.ts`:

```typescript
const API_BASE_URL = 'http://localhost:YOUR_PORT';
```

## Features

### Authentication
- **Register**: Create a new account with username, password, and personal information
- **Login**: Authenticate with username and password
- **JWT Token**: Automatically stored and sent with all authenticated requests

### Main Sections

1. **Movies**
   - View all movies
   - View top-rated movies
   - Browse movies by genre

2. **Shows**
   - View all TV shows
   - View top-rated shows
   - Browse shows by genre

3. **Genres**
   - View all genres
   - Click a genre to see related content

4. **Reviews**
   - View all reviews
   - Create new reviews (requires Profile ID and Content ID)
   - See ratings and comments

5. **Profiles**
   - View your profiles
   - Create new profiles
   - Delete profiles

6. **Account**
   - View account information
   - Edit personal details (name, phone, email)

7. **Plans**
   - View available subscription plans
   - See plan details and pricing

8. **Subscriptions**
   - View active subscription
   - Subscribe to a plan
   - Cancel subscription

## API Endpoints Used

The frontend connects to the following backend endpoints:

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login
- `POST /auth/change-password` - Change password

### Users
- `GET /users/me` - Get current user
- `PATCH /users/me` - Update username
- `DELETE /users/me` - Delete account

### Accounts
- `GET /accounts/me` - Get user's account
- `PATCH /accounts/{id}` - Update account details

### Content
- `GET /content` - List all content
- `GET /content/{id}` - Get content by ID

### Movies
- `GET /movies` - List all movies
- `GET /movies/{id}` - Get movie by ID
- `GET /movies/top-movies` - Get top-rated movies
- `GET /movies/genre/{genreId}` - Get movies by genre

### Shows
- `GET /shows` - List all shows
- `GET /shows/{id}` - Get show by ID
- `GET /shows/top-10-shows` - Get top-rated shows
- `GET /shows/genre/{genreId}` - Get shows by genre

### Genres
- `GET /genres` - List all genres
- `GET /genres/{id}` - Get genre by ID

### Profiles
- `GET /profiles/me` - Get user's profiles
- `POST /profiles` - Create profile
- `PATCH /profiles/{id}` - Update profile
- `DELETE /profiles/{id}` - Delete profile

### Reviews
- `GET /reviews` - List all reviews
- `POST /reviews` - Create review
- `PATCH /reviews/{id}` - Update review
- `DELETE /reviews/{id}` - Delete review

### Plans
- `GET /plans` - List all plans

### Subscriptions
- `GET /subscriptions/me` - Get user's subscription
- `POST /subscriptions` - Create subscription
- `DELETE /subscriptions/{id}` - Cancel subscription

## Notes

- All authenticated endpoints require a valid JWT token
- The token is automatically included in request headers after login
- Some features require specific IDs (e.g., Profile ID for reviews) - these can be found by viewing the respective sections first
- The UI is minimal and functional, focusing on clarity over design
- Error messages are displayed when API calls fail

## Troubleshooting

**CORS Issues**: If you encounter CORS errors, ensure the backend has CORS configured to allow requests from `http://localhost:5173`

**401 Unauthorized**: Your token may have expired. Logout and login again.

**Connection Refused**: Ensure the backend server is running on port 8080.

**404 Not Found**: The endpoint may not exist or the backend route may be different. Check the backend controller mappings.
