import BaseView from "./BaseView.tsx";
import {useTranslation} from "react-i18next";
import privateRegistryConfig from "../../../type/RegistryConfig/PrivateRegistryConfig.tsx";

export default function ViewPrivate() {

    const {t} = useTranslation();

    return <div className="view-registry-private">
        <h2>{t("view_registry_private")}</h2>
        <BaseView config={privateRegistryConfig}/>
    </div>
}