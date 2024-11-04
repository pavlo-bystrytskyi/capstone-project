import {useTranslation} from "react-i18next";
import NewRegistryData from "../../dto/NewRegistryData.tsx";

export default function RegistrySuccess({data}: { data?: NewRegistryData }) {
    const {t} = useTranslation();

    return (
        data ?
            <>
                <h2>{t("registry_created")}</h2>
                <div className="new-registry-data">
                    <div className="private-data">
                        <p>{t("private_link")}</p>
                        <p>{data.edit_link}</p>
                        <p>{t("private_link_description")}</p>
                    </div>
                    <div className="public-data">
                        <p>{t("public_link")}</p>
                        <p>{data.show_link}</p>
                        <p>{t("public_link_description")}</p>
                    </div>
                </div>
            </> :
            <h1>Error: No data available</h1>
    )
}