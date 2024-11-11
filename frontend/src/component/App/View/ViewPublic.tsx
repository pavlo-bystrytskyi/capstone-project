import BaseView from "./BaseView.tsx";
import {useTranslation} from "react-i18next";
import publicRegistryConfig from "../../../type/RegistryConfig/PublicRegistryConfig.tsx";

export default function ViewPublic() {

    const {t} = useTranslation();

    return <div className="view-registry-public">
        <h2>{t("view_registry_public")}</h2>
        <BaseView config={publicRegistryConfig}/>
    </div>
}