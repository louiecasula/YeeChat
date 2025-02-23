// userApi.js
const API_BASE_URL = 'http://localhost:8080/api';

export function registerUser(userData) {
    const { username, email, password } = userData;

    return fetch(`${API_BASE_URL}/users/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, email, password })
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => {
                    throw new Error(`Server error: ${data.message || response.status}`);
                });
            }
            return response.json();
        })
        .catch((error) => {
            console.error('Error registering user:', error);
            throw error;
        });
}

export function loginUser(username, password) {
    return fetch(`${API_BASE_URL}/users/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username, password })
    })
        .then(response => {
            if (!response.ok) {
                // Convert non-2xx HTTP responses into errors
                return response.json().then(data => {
                    throw new Error(`Server error: ${data.message || response.status}`);
                });
            }
            return response.json();
        })
        .catch((error) => {
            console.error('Error logging in user:', error);
            throw error; // Re-throw to allow the caller to handle it
        });
}

