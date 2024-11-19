import RegistryType from "../../../type/RegistryType.tsx";
import {useTranslation} from "react-i18next";

export default function TypeSelector({type}: {readonly type: RegistryType }) {
    const {t} = useTranslation();

    return (
        <div className="text-center">
            <h2 className="m-0">{t(type.description)}</h2>
        </div>
    );
}