async function checkLoginStatus() {
    const token = localStorage.getItem('token');
    if (!token) {
        handleLoggedOut();
        return false;
    }

    try {
        const response = await fetch('/api/members/me', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            // 토큰이 만료되었거나 유효하지 않은 경우
            localStorage.removeItem('token');
            handleLoggedOut();
            return false;
        }

        handleLoggedIn();
        return true;
    } catch (error) {
        console.error('Auth check failed:', error);
        localStorage.removeItem('token');
        handleLoggedOut();
        return false;
    }
}

// fetchWithAuth 함수 추가
async function fetchWithAuth(url, options = {}) {
    const token = localStorage.getItem('token');
    if (!token) {
        throw new Error('No token found');
    }

    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };

    if (options.method === 'POST' || options.method === 'PUT') {
        if (!(options.body instanceof FormData)) {
            headers['Content-Type'] = 'application/json';
        }
    }

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        localStorage.removeItem('token');
        handleLoggedOut();
        window.location.href = '/login.html';
        throw new Error('Authentication failed');
    }

    return response;
}

function handleLoggedIn() {
    // 로그인 상태 UI 업데이트
    const loginBtn = document.getElementById('login-btn');
    const signupBtn = document.getElementById('signup-btn');
    const profileIcon = document.getElementById('profile-icon');

    if (loginBtn) loginBtn.style.display = 'none';
    if (signupBtn) signupBtn.style.display = 'none';
    if (profileIcon) profileIcon.classList.remove('hidden');
}

function handleLoggedOut() {
    // 로그아웃 상태 UI 업데이트
    const loginBtn = document.getElementById('login-btn');
    const signupBtn = document.getElementById('signup-btn');
    const profileIcon = document.getElementById('profile-icon');

    if (loginBtn) loginBtn.style.display = 'inline-block';
    if (signupBtn) signupBtn.style.display = 'inline-block';
    if (profileIcon) profileIcon.classList.add('hidden');
}

function logout() {
    localStorage.removeItem('token');
    handleLoggedOut();
    window.location.href = '/login.html';
}

// 모든 함수를 export
const auth = {
    checkLoginStatus,
    fetchWithAuth,
    handleLoggedIn,
    handleLoggedOut,
    logout
};

// auth 객체를 전역으로 사용할 수 있도록 설정
window.auth = auth;