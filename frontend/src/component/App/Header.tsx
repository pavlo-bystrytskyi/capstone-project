import {useTranslation} from "react-i18next";
import User from "../../type/User.tsx";

export default function Header({user}: { user: User }) {
    const {t} = useTranslation();

    const isLoggedIn = !!user.email;

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/oauth2/authorization/google', '_self')
    }

    const logout = () => {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + "/api/auth/logout", '_self');
    };
    const handleButtonClick = () => {
        if (isLoggedIn) {
            logout();
        } else {
            login();
        }
    };

    return (
        <nav>
            <div>
                <span>Hello, {isLoggedIn && user?.firstName || t("Guest")}
                    {isLoggedIn && <img src={user?.picture}/>}
                    </span>
                {isLoggedIn && <div><a href="/show-all">{t("open_all_registries")}</a></div>}
            </div>
            <button onClick={handleButtonClick}>
                {isLoggedIn ? 'Logout' : 'Login'}
            </button>
        </nav>
    );
}
