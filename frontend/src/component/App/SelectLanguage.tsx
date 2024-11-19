import LanguageSelector from "./SelectLanguage/LanguageSelector.tsx";
import {SupportedLanguageCode, supportedLanguages} from "../../type/SupportedLanguage.tsx";
import { Dropdown, DropdownButton, ButtonGroup } from 'react-bootstrap';
import { useTranslation } from 'react-i18next';

export default function SelectLanguage() {
    const { t, i18n } = useTranslation();

    const setLanguage = (languageCode: SupportedLanguageCode) => {
        if (languageCode !== i18n.resolvedLanguage && Object.values(SupportedLanguageCode).includes(languageCode)) {
            i18n.changeLanguage(languageCode);
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
