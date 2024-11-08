import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../dto/RegistryIdData.tsx";
import {useTranslation} from "react-i18next";
import privateRegistryConfig from "../../../type/RegistryConfig/PrivateRegistryConfig.tsx";

export default function CreateGuest({
                                    onSuccess
                                }: {
    readonly onSuccess: (data: RegistryIdData) => void
}) {

    const {t} = useTranslation();

    return (
        <div className="new-registry-guest">
            <h2>{t("new_registry")}</h2>
            <BaseEdit onSuccess={onSuccess} config={privateRegistryConfig}/>
        </div>
    );
}
