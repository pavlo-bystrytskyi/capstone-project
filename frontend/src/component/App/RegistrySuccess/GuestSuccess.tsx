import RegistryIdData from "../../../type/RegistryIdData.tsx";
import {Alert} from "react-bootstrap";
import BaseSuccess from "./BaseSuccess.tsx";
import {useTranslation} from "react-i18next";

export default function GuestSuccess({data}: { readonly data?: RegistryIdData }) {
    const {t} = useTranslation();
    const host = window.location.origin
    const publicLink = data ? host + "/show-public/" + data.publicId : host;
    const privateLink = data ? host + "/show-private/" + data.privateId : host;

    return data ? (
        <BaseSuccess privateLink={privateLink} publicLink={publicLink}/>
    ) : (
        <Alert variant="danger" className="text-center">
            <h1>{t("error_no_data")}</h1>
        </Alert>
    );
}