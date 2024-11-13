import {useTranslation} from "react-i18next";
import SelectLanguage from "./App/SelectLanguage.tsx";
import ".././style/App.css"

import {Route, Routes, useNavigate} from "react-router-dom";
import SelectType from "./App/SelectType.tsx";
import {SupportedLanguageCode, supportedLanguages} from "../type/SupportedLanguage.tsx";
import {RegistryTypeCode, registryTypes} from "../type/RegistryType.tsx";
import {useEffect, useState} from "react";
import RegistrySuccess from "./App/RegistrySuccess.tsx";
import RegistryIdData from "../dto/RegistryIdData.tsx";
import ViewPublic from "./App/View/ViewPublic.tsx";
import ViewPrivate from "./App/View/ViewPrivate.tsx";
import CreateGuest from "./App/Edit/CreateGuest.tsx";
import EditGuest from "./App/Edit/EditGuest.tsx";
import Header from "./App/Header.tsx";
import Footer from "./App/Footer.tsx";

function App() {
    const {i18n} = useTranslation();
    const navigate = useNavigate();
    const [registryTypeCode, setRegistryTypeCode] = useState<RegistryTypeCode>();
    const [registryIdData, setRegistryIdData] = useState<RegistryIdData>()
    const setLanguage = function (languageCode: SupportedLanguageCode) {
        if (languageCode !== i18n.resolvedLanguage && Object.values(SupportedLanguageCode).includes(languageCode)) {
            i18n.changeLanguage(languageCode);
        }
        navigate("/type");
    };

    const redirectToSuccess = function (data: RegistryIdData) {
        setRegistryIdData(data);
        navigate("/success-guest");
    }

    useEffect(() => {
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.GUEST) navigate("/new-guest");
    }, [registryTypeCode]);

    return (
        <>
            <Header/>
            <Routes>
                <Route path="/"
                       element={<SelectLanguage languages={supportedLanguages} setLanguage={setLanguage}/>}/>
                <Route path="/type"
                       element={<SelectType types={registryTypes} setRegistryType={setRegistryTypeCode}/>}/>
                <Route path="/new-guest" element={<CreateGuest onSuccess={redirectToSuccess}/>}/>
                <Route path="/edit-guest/:id" element={<EditGuest onSuccess={redirectToSuccess}/>}/>
                <Route path="/success-guest" element={<RegistrySuccess data={registryIdData}/>}/>
                <Route path="/show-public/:id" element={<ViewPublic/>}/>
                <Route path="/show-private/:id" element={<ViewPrivate/>}/>
            </Routes>
            <Footer/>
        </>
    )
}

export default App
