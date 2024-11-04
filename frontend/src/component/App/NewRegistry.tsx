import {useTranslation} from "react-i18next";
import NewRegistryData from "../../dto/NewRegistryData.tsx";

export default function NewRegistry({onSuccess}: {readonly onSuccess: (data: NewRegistryData) => void }) {
    const {t} = useTranslation();

    return (
        <div className="new-registry-guest">
            <h2>{t("new_registry")}</h2>
        </div>
    );
}