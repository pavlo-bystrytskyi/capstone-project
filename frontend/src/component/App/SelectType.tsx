import RegistryType, {RegistryTypeCode} from "../../type/RegistryType.tsx";
import TypeSelector from "./SelectType/TypeSelector.tsx";
import {Button, Col, Row} from "react-bootstrap";
import useAuth from "../../context/auth/UserAuth.tsx";
import {useTranslation} from "react-i18next";

export default function SelectType(
    {
        types,
        setRegistryType,
    }: {
        readonly types: RegistryType[];
        readonly setRegistryType: (type: RegistryTypeCode) => void;
    }
) {
    const {user} = useAuth();
    const {t} = useTranslation();

    const typeSelectors = types.map((type) => {
        const restricted = type.restricted && !user.email;

        return <div key={type.code} className="col-6 col-sm-4 col-md-3 m-2 d-flex justify-content-center">
            <Col>
                <Row>
                    <Button
                        variant="outline-primary"
                        onClick={() => setRegistryType(type.code)}
                        className="square-card d-flex justify-content-center align-items-center w-100"
                        disabled={restricted}
                    >
                        <TypeSelector type={type}/>
                    </Button>
                </Row>
                {restricted &&
                    <Row>
                        <p className="text-muted text-center mt-2">
                            {t("hint_restricted")}
                        </p>
                    </Row>
                }
            </Col>
        </div>
    });

    return (
        <div className="w-100">
            <div className="row justify-content-center">
                {typeSelectors}
            </div>
        </div>
    );
}
