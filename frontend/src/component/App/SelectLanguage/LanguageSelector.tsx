
import {SupportedLanguage} from "../../../type/SupportedLanguage.tsx";

export default function LanguageSelector({ language }: { readonly language: SupportedLanguage }) {
    return (
        <div className="d-flex align-items-center">
            <img
                alt={`${language.description} flag`}
                src={`/locales/${language.code}/img.png`}
                style={{ width: "20px", height: "auto", marginRight: '8px' }}
            />
            <span>{language.description}</span>
        </div>
    );
}
