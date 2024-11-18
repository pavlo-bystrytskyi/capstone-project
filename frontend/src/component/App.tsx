import {useTranslation} from "react-i18next";
import SelectLanguage from "./App/SelectLanguage.tsx";
import ".././style/App.css"

import {Route, Routes, useNavigate} from "react-router-dom";
import SelectType from "./App/SelectType.tsx";
import {SupportedLanguageCode, supportedLanguages} from "../type/SupportedLanguage.tsx";
import {RegistryTypeCode, registryTypes} from "../type/RegistryType.tsx";
import {useEffect, useState} from "react";
import RegistryIdData from "../type/RegistryIdData.tsx";
import ViewPublic from "./App/View/ViewPublic.tsx";
import ViewPrivate from "./App/View/ViewPrivate.tsx";
import CreateGuest from "./App/Edit/CreateGuest.tsx";
import EditGuest from "./App/Edit/EditGuest.tsx";
import Header from "./App/Header.tsx";
import Footer from "./App/Footer.tsx";
import CreateUser from "./App/Edit/CreateUser.tsx";
import GuestSuccess from "./App/RegistrySuccess/GuestSuccess.tsx";
import UserSuccess from "./App/RegistrySuccess/UserSuccess.tsx";
import ViewUser from "./App/View/ViewUser.tsx";
import EditUser from "./App/Edit/EditUser.tsx";
import ViewList from "./App/View/ViewList.tsx";
import axios from "axios";
import User from "../type/User.tsx";
import emptyUser from "../type/EmptyUser.tsx";

function App() {
    const {i18n} = useTranslation();
    const navigate = useNavigate();
    const [user, setUser] = useState<User>(emptyUser);
    const [registryTypeCode, setRegistryTypeCode] = useState<RegistryTypeCode>();
    const [registryIdData, setRegistryIdData] = useState<RegistryIdData>()
    const setLanguage = function (languageCode: SupportedLanguageCode) {
        if (languageCode !== i18n.resolvedLanguage && Object.values(SupportedLanguageCode).includes(languageCode)) {
            i18n.changeLanguage(languageCode);
        }
        navigate("/type");
    };

    const redirectToSuccessGuest = function (data: RegistryIdData) {
        setRegistryIdData(data);
        navigate("/success-guest");
    }

    const redirectToSuccessUser = function (data: RegistryIdData) {
        setRegistryIdData(data);
        navigate("/success-user");
    }

    const loadUser = () => {
        axios.get<User>('/api/auth/me')
            .then(response => {
                setUser(response.data);
            })
            .catch(() => {
                setUser(emptyUser);
            })
    }

    useEffect(() => {
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.GUEST) navigate("/new-guest");
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.CUSTOMER) navigate("/new-user");
    }, [registryTypeCode]);

    useEffect(() => {
        loadUser()
    }, []);

    return (
        <>
            <Header user={user}/>
            <Routes>
                <Route path="/"
                       element={<SelectLanguage languages={supportedLanguages} setLanguage={setLanguage}/>}/>
                <Route path="/type"
                       element={<SelectType types={registryTypes} setRegistryType={setRegistryTypeCode}/>}/>
                <Route path="/new-guest" element={<CreateGuest onSuccess={redirectToSuccessGuest}/>}/>
                <Route path="/new-user" element={<CreateUser onSuccess={redirectToSuccessUser}/>}/>
                <Route path="/edit-guest/:id" element={<EditGuest onSuccess={redirectToSuccessGuest}/>}/>
                <Route path="/edit-user/:id" element={<EditUser onSuccess={redirectToSuccessUser}/>}/>
                <Route path="/success-guest" element={<GuestSuccess data={registryIdData}/>}/>
                <Route path="/success-user" element={<UserSuccess data={registryIdData}/>}/>
                <Route path="/show-public/:id" element={<ViewPublic/>}/>
                <Route path="/show-private/:id" element={<ViewPrivate/>}/>
                <Route path="/show-user/:id" element={<ViewUser/>}/>
                <Route path="/show-all" element={<ViewList user={user}/>}/>
            </Routes>
            <Footer/>
        </>
    )
}

export default App
