import RegistryType from "../../../type/RegistryType.tsx";
import {useTranslation} from "react-i18next";

export default function TypeSelector({type}: { type: RegistryType }) {
    const {t} = useTranslation();

    return (<div className="select-type">
        <h2>{t(type.description)}</h2>
    </div>)
}