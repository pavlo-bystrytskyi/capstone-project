import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../dto/RegistryIdData.tsx";
import {useTranslation} from "react-i18next";
import privateRegistryConfig from "../../../type/RegistryConfig/PrivateRegistryConfig.tsx";

export default function EditGuest({
                                      onSuccess
                                  }: {
    readonly onSuccess: (data: RegistryIdData) => void
}) {

    const {t} = useTranslation();

    return (
        <div className="edit-registry-guest">
            <h2>{t("registry_edit")}</h2>
            <BaseEdit onSuccess={onSuccess} config={privateRegistryConfig}/>
        </div>
    );
}
