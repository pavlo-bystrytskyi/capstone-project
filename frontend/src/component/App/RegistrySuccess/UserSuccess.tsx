import RegistryIdData from "../../../type/RegistryIdData.tsx";
import BaseSuccess from "./BaseSuccess.tsx";
import {Alert} from "react-bootstrap";
import {useTranslation} from "react-i18next";
import ProtectedComponent from "../../ProtectedComponent.tsx";

export default function UserSuccess({data}: { readonly data?: RegistryIdData }) {
    const {t} = useTranslation();
    const host = window.location.origin
    const publicLink = data ? host + "/show-public/" + data.publicId : host;
    const privateLink = data ? host + "/show-user/" + data.privateId : host;

    return data ? (
        <ProtectedComponent>
            <BaseSuccess privateLink={privateLink} publicLink={publicLink}/>
        </ProtectedComponent>
    ) : (
        <Alert variant="danger" className="text-center">
            <h1>{t("error_no_data")}</h1>
        </Alert>
    );
}