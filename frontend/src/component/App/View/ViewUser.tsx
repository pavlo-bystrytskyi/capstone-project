import BaseView from "./BaseView.tsx";
import {useTranslation} from "react-i18next";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";

export default function ViewUser() {

    const {t} = useTranslation();

    return <div className="view-registry-user">
        <h2>{t("view_registry_user")}</h2>
        <BaseView config={userRegistryConfig}/>
    </div>
}