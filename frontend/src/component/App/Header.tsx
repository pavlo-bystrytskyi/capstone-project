import axios from "axios";
import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import User from "../../type/User.tsx";
import emptyUser from "../../type/EmptyUser.tsx";

export default function Header() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [user, setUser] = useState<User>(emptyUser);
    const {t} = useTranslation();

    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/oauth2/authorization/google', '_self')
    }

    const loadUser = () => {
        axios.get<User>('/api/auth/me')
            .then(response => {
                setUser(response.data);
                setIsLoggedIn(!!response.data.email);
            })
            .catch(() => {
                setUser(emptyUser);
                setIsLoggedIn(false);
            })
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

    useEffect(() => {
        loadUser()
    }, []);

    return (
        <nav>
            <div>
                Hello, {isLoggedIn && user?.firstName || t("Guest")}
                {isLoggedIn && <img src={user?.picture}/>}
            </div>
            <button onClick={handleButtonClick}>
                {isLoggedIn ? 'Logout' : 'Login'}
            </button>
        </nav>
    );
}
