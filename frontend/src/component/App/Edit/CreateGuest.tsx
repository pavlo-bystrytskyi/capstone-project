import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import privateRegistryConfig from "../../../type/RegistryConfig/PrivateRegistryConfig.tsx";

export default function CreateGuest(
    {
        onSuccess
    }: {
        readonly onSuccess: (data: RegistryIdData) => void
    }
) {

    return (
        <BaseEdit onSuccess={onSuccess} config={privateRegistryConfig}/>
    );
}
