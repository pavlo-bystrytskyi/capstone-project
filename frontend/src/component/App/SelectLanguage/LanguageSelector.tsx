import "../../../style/src/component/App/SelectLanguage/LanguageSelector.css";
import {SupportedLanguage} from "../../../type/SupportedLanguage.tsx";

export default function LanguageSelector({language}: { language: SupportedLanguage }) {
    return <div className="select-language-flag">
        <img alt={language.description+ " flag"} src={"/locales/" + language.code + "/img.png"}/>
        <h2>{language.description}</h2>
    </div>
}