import BaseView from "./BaseView.tsx";
import userRegistryConfig from "../../../type/RegistryConfig/UserRegistryConfig.tsx";
import ProtectedComponent from "../../ProtectedComponent.tsx";

export default function ViewUser() {

    return <ProtectedComponent>
        <BaseView config={userRegistryConfig}/>
    </ProtectedComponent>
}
