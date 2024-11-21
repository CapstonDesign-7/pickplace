// auth.js - 공통으로 사용할 인증 관련 함수
const auth = {
    // 토큰 체크 및 API 요청 함수
    async fetchWithAuth(url, options = {}) {
        const token = localStorage.getItem('token');
        if (!token) {
            auth.handleLogout('로그인이 필요합니다.');
            return null;
        }

        try {
            const headers = {
                ...options.headers,
                'Authorization': `Bearer ${token}`
            };

            const response = await fetch(url, { ...options, headers });

            if (response.status === 401) {
                // 토큰이 만료되었거나 유효하지 않은 경우
                auth.handleLogout('로그인 시간이 만료되었습니다. 다시 로그인해 주세요.');
                return null;
            }

            return response;
        } catch (error) {
            console.error('API 요청 실패:', error);
            throw error;
        }
    },

    // 로그아웃 처리 함수
    handleLogout(message) {
        localStorage.removeItem('token');
        alert(message);
        window.location.href = '/login.html';
    },

    // 토큰 유효성 주기적 체크
    startTokenCheck() {
        // 5분마다 토큰 유효성 체크
        setInterval(async () => {
            try {
                const response = await this.fetchWithAuth('/api/members/validate-token');
                if (!response || !response.ok) {
                    this.handleLogout('로그인 시간이 만료되었습니다. 다시 로그인해 주세요.');
                }
            } catch (error) {
                console.error('토큰 검증 실패:', error);
            }
        }, 5 * 60 * 1000); // 5분
    }
};