import LanguageSelector from "./SelectLanguage/LanguageSelector.tsx";
import {SupportedLanguageCode, supportedLanguages} from "../../type/SupportedLanguage.tsx";
import { Dropdown, DropdownButton, ButtonGroup } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';
import {useEffect} from "react";

export default function SelectLanguage() {
    const { t, i18n } = useTranslation();
    const setLanguage = (languageCode: SupportedLanguageCode) => {
        if (languageCode !== i18n.resolvedLanguage && Object.values(SupportedLanguageCode).includes(languageCode)) {
            i18n.changeLanguage(languageCode);
            localStorage.setItem('language', languageCode);
        }
    };
    const initializeLanguage = () => {
        const savedLanguage = localStorage.getItem('language');
        if (savedLanguage && Object.values(SupportedLanguageCode).includes(savedLanguage as SupportedLanguageCode)) {
            i18n.changeLanguage(savedLanguage as SupportedLanguageCode);
        }
    };
    const languageSelectors = supportedLanguages.map((language) => (
        <Dropdown.Item
            key={language.code}
            onClick={() => setLanguage(language.code)}
            className="d-flex align-items-center"
            style={{ width: 'auto' }}
        >
            <LanguageSelector language={language} />
        </Dropdown.Item>
    ));
    useEffect(() => {
        initializeLanguage();
    }, []);

    return (
        <DropdownButton
            as={ButtonGroup}
            id="dropdown-custom-components"
            variant="outline-primary"
            title={<span style={{ fontSize: '18px' }}>🌐 {t("change_language")}</span>} // Символ для обозначения языка
            className="select-language ms-3"
        >
            {languageSelectors}
        </DropdownButton>
    );
}
