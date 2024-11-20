import {ReactNode, useEffect, useState} from "react";
import User from "../../type/User.tsx";
import emptyUser from "../../type/EmptyUser.tsx";
import axios from "axios";
import {AuthContext} from "./AuthContext.tsx";
import ToastVariant from "../toast/ToastVariant.tsx";
import {useTranslation} from "react-i18next";
import useToast from "../toast/UseToast.tsx";

export default function AuthProvider(
    {
        children
    }: {
        children: ReactNode
    }
) {
    const {t} = useTranslation();
    const {addToast} = useToast();
    const [user, setUser] = useState<User>(emptyUser);
    const loadUser = () => {
        axios.get<User>('/api/auth/me')
            .then(response => {
                setUser(response.data);
            })
            .catch(() => {
                setUser(emptyUser);
                addToast(t("toast_user_load_failed"), ToastVariant.ERROR);
            })
    }
    const login = () => {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/oauth2/authorization/google', '_self')
    }
    const logout = () => {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + "/api/auth/logout", '_self');
    };
    useEffect(() => {
        loadUser()
    }, []);

    return <AuthContext.Provider value={{user, login, logout}}>
        {children}
    </AuthContext.Provider>
};
