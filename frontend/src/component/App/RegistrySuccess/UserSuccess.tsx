import RegistryIdData from "../../../type/RegistryIdData.tsx";
import BaseSuccess from "./BaseSuccess.tsx";

export default function UserSuccess ({data}: {readonly data?: RegistryIdData }) {
    const host = window.location.origin
    const publicLink = data ? host + "/show-public/" + data.publicId : host;
    const privateLink = data ? host + "/show-user/" + data.privateId : host;

    return data
        ? <BaseSuccess privateLink={privateLink} publicLink={publicLink}/>
        : <h1>Error: No data available</h1>
}