import {useTranslation} from "react-i18next";
import SelectLanguage from "./App/SelectLanguage.tsx";
import ".././style/App.css"

import {Route, Routes, useNavigate} from "react-router-dom";
import SelectType from "./App/SelectType.tsx";
import {SupportedLanguageCode, supportedLanguages} from "../type/SupportedLanguage.tsx";
import {RegistryTypeCode, registryTypes} from "../type/RegistryType.tsx";
import {useEffect, useState} from "react";
import NewRegistry from "./App/NewRegistry.tsx";
import RegistrySuccess from "./App/RegistrySuccess.tsx";
import NewRegistryData from "../dto/NewRegistryData.tsx";
import ViewPublic from "./App/ViewPublic.tsx";


function App() {
    const {i18n} = useTranslation();
    const navigate = useNavigate();
    const [registryTypeCode, setRegistryTypeCode] = useState<RegistryTypeCode>();
    const [newRegistryData, setNewRegistryData] = useState<NewRegistryData>()
    const setLanguage = function (languageCode: SupportedLanguageCode) {
        if (languageCode !== i18n.resolvedLanguage && Object.values(SupportedLanguageCode).includes(languageCode)) {
            i18n.changeLanguage(languageCode);
        }
        navigate("/type");
    };

    const redirectToSuccess = function (data: NewRegistryData) {
        setNewRegistryData(data);
        navigate("/success-guest");
    }

    useEffect(() => {
        if (registryTypeCode && registryTypeCode === RegistryTypeCode.GUEST) navigate("/new-guest");
    }, [registryTypeCode]);

    return (
        <Routes>
            <Route path="/"
                   element={<SelectLanguage languages={supportedLanguages} setLanguage={setLanguage}/>}/>
            <Route path="/type"
                   element={<SelectType types={registryTypes} setRegistryType={setRegistryTypeCode}/>}/>
            <Route path="/new-guest" element={<NewRegistry onSuccess={redirectToSuccess}/>}/>
            <Route path="/success-guest" element={<RegistrySuccess data={newRegistryData}/>}/>
            <Route path="/show-public/:id" element={<ViewPublic/>}/>
        </Routes>
    )
}

export default App
