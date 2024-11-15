import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import {useTranslation} from "react-i18next";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";

export default function CreateUser({
                                    onSuccess
                                }: {
    readonly onSuccess: (data: RegistryIdData) => void
}) {

    const {t} = useTranslation();

    return (
        <div className="new-registry-customer">
            <h2>{t("new_registry")}</h2>
            <BaseEdit onSuccess={onSuccess} config={userRegistryConfig}/>
        </div>
    );
}
