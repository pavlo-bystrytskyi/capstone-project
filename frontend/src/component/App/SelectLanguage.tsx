import "../../style/src/component/App/SelectLanguage.css";
import LanguageSelector from "./SelectLanguage/LanguageSelector.tsx";
import {SupportedLanguage, SupportedLanguageCode} from "../../type/SupportedLanguage.tsx";

export default function SelectLanguage({languages, setLanguage}: {
    languages: SupportedLanguage[],
    setLanguage: (code: SupportedLanguageCode) => void
}) {
    const languageSelectors = languages.map(
        (language) => <li className="select-language-element" key={language.code}
                          onClick={() => setLanguage(language.code)}>
            <LanguageSelector language={language}/>
        </li>
    )

    return <div className="select-language">
        <ul className="select-language-list">
            {languageSelectors}
        </ul>
    </div>
}