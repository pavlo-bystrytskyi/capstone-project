import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import {useTranslation} from "react-i18next";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";

export default function EditUser({
                                     onSuccess
                                 }: {
    readonly onSuccess: (data: RegistryIdData) => void
}) {

    const {t} = useTranslation();

    return (
        <div className="edit-registry-user">
            <h2>{t("registry_edit")}</h2>
            <BaseEdit onSuccess={onSuccess} config={userRegistryConfig}/>
        </div>
    );
}
