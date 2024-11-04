import RegistryType, {RegistryTypeCode} from "../../type/RegistryType.tsx";
import TypeSelector from "./SelectType/TypeSelector.tsx";


export default function SelectType({types, setRegistryType}: {
    readonly types: RegistryType[],
    readonly setRegistryType: (type: RegistryTypeCode) => void
}) {

    const typeSelectors = types.map(
        (type) => {
            return <button className="select-registry-element" key={type.code}
                onClick={() => setRegistryType(type.code)}>
                <TypeSelector type={type}/>
            </button>
        }
    );

    return <div className="select-registry">
        <ul className="select-registry-list">
            {typeSelectors}
        </ul>
    </div>
}