import BaseEdit from "./BaseEdit.tsx";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";
import ProtectedComponent from "../../ProtectedComponent.tsx";

export default function CreateUser(
    {
        onSuccess
    }: {
        readonly onSuccess: (data: RegistryIdData) => void
    }
) {

    return (
        <ProtectedComponent>
            <BaseEdit onSuccess={onSuccess} config={userRegistryConfig}/>
        </ProtectedComponent>
    );
}
