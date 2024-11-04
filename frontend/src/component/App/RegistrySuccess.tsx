import {useTranslation} from "react-i18next";
import NewRegistryData from "../../dto/NewRegistryData.tsx";

export default function RegistrySuccess({data}: { data?: NewRegistryData }) {
    const {t} = useTranslation();

    const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
    const publicLink = data ? host + "/api/wishlist/public/" + data.publicId : host;
    const privateLink = data ? host + "/api/wishlist/" + data.privateId : host;

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
