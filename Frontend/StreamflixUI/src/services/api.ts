const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';

export interface AuthResponse {
  token: string;
  usersId: number;
  username: string;
  accountId: number;
}

export interface RegisterRequest {
  username: string;
  password: string;
  firstname: string;
  lastname: string;
  phonenumber: string;
  mail: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

class ApiService {
  private getHeaders(includeAuth = true): HeadersInit {
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };
    
    if (includeAuth) {
      const token = localStorage.getItem('token');
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }
    
    return headers;
  }

  async request<T>(
    endpoint: string,
    options: RequestInit = {},
    includeAuth = true
  ): Promise<T> {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: this.getHeaders(includeAuth),
    });

    if (!response.ok) {
      const text = await response.text();
      try {
        const json = JSON.parse(text);
        throw new Error(json.message || json.error || text || `HTTP error! status: ${response.status}`);
      } catch (parseErr) {
        if (parseErr instanceof SyntaxError) {
          throw new Error(text || `HTTP error! status: ${response.status}`);
        }
        throw parseErr;
      }
    }

    if (response.status === 204) {
      return {} as T;
    }

    return response.json();
  }

  async register(data: RegisterRequest): Promise<AuthResponse> {
    return this.request<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    }, false);
  }

  async login(data: LoginRequest): Promise<AuthResponse> {
    return this.request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }, false);
  }

  async changePassword(oldPassword: string, newPassword: string): Promise<void> {
    return this.request('/auth/change-password', {
      method: 'POST',
      body: JSON.stringify({ oldPassword, newPassword }),
    });
  }

  async getUsers(): Promise<any[]> {
    return this.request('/users');
  }

  async getUserById(id: number): Promise<any> {
    return this.request(`/users/${id}`);
  }

  async getMe(): Promise<any> {
    return this.request('/users/me');
  }

  async updateMe(data: any): Promise<any> {
    return this.request('/users/me', {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteMe(): Promise<void> {
    return this.request('/users/me', {
      method: 'DELETE',
    });
  }

  async getAccounts(): Promise<any[]> {
    return this.request('/accounts');
  }

  async getAccountById(id: number): Promise<any> {
    return this.request(`/accounts/${id}`);
  }

  async getMyAccount(): Promise<any> {
    return this.request('/accounts/me');
  }

  async updateAccount(id: number, data: any): Promise<any> {
    return this.request(`/accounts/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async patchAccount(id: number, data: any): Promise<any> {
    return this.request(`/accounts/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async getContent(): Promise<any[]> {
    return this.request('/content');
  }

  async getContentById(id: number): Promise<any> {
    return this.request(`/content/${id}`);
  }

  async createContent(data: any): Promise<any> {
    return this.request('/content', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateContent(id: number, data: any): Promise<any> {
    return this.request(`/content/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async patchContent(id: number, data: any): Promise<any> {
    return this.request(`/content/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteContent(id: number): Promise<void> {
    return this.request(`/content/${id}`, {
      method: 'DELETE',
    });
  }

  async getMovies(): Promise<any[]> {
    return this.request('/movies');
  }

  async getMovieById(id: number): Promise<any> {
    return this.request(`/movies/${id}`);
  }

  async getTopMovies(): Promise<any[]> {
    return this.request('/movies/top-movies');
  }

  async getMoviesByGenre(genreId: number): Promise<any[]> {
    return this.request(`/movies/genre/${genreId}`);
  }

  async patchMovie(id: number, data: any): Promise<any> {
    return this.request(`/movies/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteMovie(id: number): Promise<void> {
    return this.request(`/movies/${id}`, {
      method: 'DELETE',
    });
  }

  async getShows(): Promise<any[]> {
    return this.request('/shows');
  }

  async getShowById(id: number): Promise<any> {
    return this.request(`/shows/${id}`);
  }

  async getTopShows(): Promise<any[]> {
    return this.request('/shows/top-10-shows');
  }

  async getShowsByGenre(genreId: number): Promise<any[]> {
    return this.request(`/shows/genre/${genreId}`);
  }

  async updateShow(id: number, data: any): Promise<any> {
    return this.request(`/shows/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async patchShow(id: number, data: any): Promise<any> {
    return this.request(`/shows/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteShow(id: number): Promise<void> {
    return this.request(`/shows/${id}`, {
      method: 'DELETE',
    });
  }

  async getGenres(): Promise<any[]> {
    return this.request('/genres');
  }

  async getGenreById(id: number): Promise<any> {
    return this.request(`/genres/${id}`);
  }

  async createGenre(data: any): Promise<any> {
    return this.request('/genres', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateGenre(id: number, data: any): Promise<any> {
    return this.request(`/genres/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deleteGenre(id: number): Promise<void> {
    return this.request(`/genres/${id}`, {
      method: 'DELETE',
    });
  }

  async getMyProfiles(): Promise<any[]> {
    return this.request('/profiles/me');
  }

  async getAllProfiles(): Promise<any[]> {
    return this.request('/profiles');
  }

  async getProfileById(id: number): Promise<any> {
    return this.request(`/profiles/${id}`);
  }

  async createProfile(data: any): Promise<any> {
    return this.request('/profiles', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async patchProfile(id: number, data: any): Promise<any> {
    return this.request(`/profiles/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteProfile(id: number): Promise<void> {
    return this.request(`/profiles/${id}`, {
      method: 'DELETE',
    });
  }

  async getReviews(): Promise<any[]> {
    return this.request('/reviews');
  }

  async getReviewById(id: number): Promise<any> {
    return this.request(`/reviews/${id}`);
  }

  async createReview(data: any): Promise<any> {
    return this.request('/reviews', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateReview(id: number, data: any): Promise<any> {
    return this.request(`/reviews/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async patchReview(id: number, data: any): Promise<any> {
    return this.request(`/reviews/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteReview(id: number): Promise<void> {
    return this.request(`/reviews/${id}`, {
      method: 'DELETE',
    });
  }

  async getPlans(): Promise<any[]> {
    return this.request('/plans');
  }

  async getPlanById(id: number): Promise<any> {
    return this.request(`/plans/${id}`);
  }

  async createPlan(data: any): Promise<any> {
    return this.request('/plans', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updatePlan(id: number, data: any): Promise<any> {
    return this.request(`/plans/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async deletePlan(id: number): Promise<void> {
    return this.request(`/plans/${id}`, {
      method: 'DELETE',
    });
  }

  async getMySubscription(): Promise<any> {
    return this.request('/subscriptions/me');
  }

  async getAllSubscriptions(): Promise<any[]> {
    return this.request('/subscriptions');
  }

  async getSubscriptionById(id: number): Promise<any> {
    return this.request(`/subscriptions/${id}`);
  }

  async createSubscription(data: any): Promise<any> {
    return this.request('/subscriptions', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async updateSubscription(id: number, data: any): Promise<any> {
    return this.request(`/subscriptions/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  async patchSubscription(id: number, data: any): Promise<any> {
    return this.request(`/subscriptions/${id}`, {
      method: 'PATCH',
      body: JSON.stringify(data),
    });
  }

  async deleteSubscription(id: number): Promise<void> {
    return this.request(`/subscriptions/${id}`, {
      method: 'DELETE',
    });
  }

  async getMyPaymentMethods(): Promise<any[]> {
    return this.request('/payment-methods/me');
  }

  async createPaymentMethod(data: any): Promise<any> {
    return this.request('/payment-methods', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async createPayment(data: any): Promise<any> {
    return this.request('/payments', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getMyReceipts(): Promise<any[]> {
    return this.request('/receipts/me');
  }
}

export const api = new ApiService();
