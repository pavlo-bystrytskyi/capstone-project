import i18n from "i18next";
import {initReactI18next} from "react-i18next";
import HttpApi from "i18next-http-backend";
import {SupportedLanguageCode} from "../type/SupportedLanguage.tsx";

i18n
    .use(initReactI18next)
    .use(HttpApi)
    .init({
        lng: "en",
        fallbackLng: "en",
        supportedLngs: Object.values(SupportedLanguageCode),
        debug: true,
        interpolation: {
            escapeValue: false,
        },
    });

export default i18n;