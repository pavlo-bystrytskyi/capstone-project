import {useTranslation} from "react-i18next";
import RegistryIdData from "../../dto/RegistryIdData.tsx";

export default function RegistrySuccess({data}: { data?: RegistryIdData }) {
    const {t} = useTranslation();

    const host = window.location.origin
    const publicLink = data ? host + "/show-public/" + data.publicId : host;
    const privateLink = data ? host + "/show-private/" + data.privateId : host;

    return (
        data ?
            <>
                <h2>{t("registry_created")}</h2>
                <div className="new-registry-data">
                    <div className="private-data">
                        <p>{t("private_link")}</p>
                        <p>{privateLink}</p>
                        <button onClick={() => window.open(privateLink)}>{t("open_link")}</button>
                        <button onClick={() => {
                            navigator.clipboard.writeText(privateLink)
                        }}>{t("copy_link")}
                        </button>
                        <p>{t("private_link_description")}</p>
                    </div>
                    <div className="public-data">
                        <p>{t("public_link")}</p>
                        <p>{publicLink}</p>
                        <button onClick={() => window.open(publicLink)}>{t("open_link")}</button>
                        <button onClick={() => {
                            navigator.clipboard.writeText(publicLink)
                        }}>{t("copy_link")}
                        </button>
                        <p>{t("public_link_description")}</p>
                    </div>
                </div>
            </> :
            <h1>Error: No data available</h1>
    )
}
