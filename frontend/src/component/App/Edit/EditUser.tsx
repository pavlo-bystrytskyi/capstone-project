import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";

export default function EditUser(
    {
        onSuccess
    }: {
        readonly onSuccess: (data: RegistryIdData) => void
    }
) {

    return (
        <BaseEdit onSuccess={onSuccess} config={userRegistryConfig}/>
    );
}
