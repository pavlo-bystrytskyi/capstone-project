import {useTranslation} from "react-i18next";

export default function BaseSuccess(
    {
        privateLink,
        publicLink
    }: {
        readonly privateLink: string,
        readonly publicLink: string
    }
) {
    const {t} = useTranslation();

    return (
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
            </>
    )
}
