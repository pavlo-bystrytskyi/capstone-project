import RegistryType, {RegistryTypeCode} from "../../type/RegistryType.tsx";
import TypeSelector from "./SelectType/TypeSelector.tsx";
import {Button} from "react-bootstrap";

export default function SelectType(
    {
        types,
        setRegistryType,
    }: {
        readonly types: RegistryType[];
        readonly setRegistryType: (type: RegistryTypeCode) => void;
    }
) {
    const typeSelectors = types.slice(0, 2).map((type) => (
        <div key={type.code} className="col-6 col-sm-4 col-md-3 m-2 d-flex justify-content-center">
            <Button
                variant="outline-primary"
                onClick={() => setRegistryType(type.code)}
                className="square-card d-flex justify-content-center align-items-center w-100"
            >
                <TypeSelector type={type}/>
            </Button>
        </div>
    ));

    return (
        <div className="w-100">
            <div className="row justify-content-center">
                {typeSelectors}
            </div>
        </div>
    );
}
