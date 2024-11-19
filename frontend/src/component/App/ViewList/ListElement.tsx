import Registry from "../../../type/Registry.tsx";
import {Link} from "react-router-dom";
import {Col, Row} from "react-bootstrap";

export default function ListElement({registry}: { registry: Registry }) {
    const host = window.location.origin
    const link = host + "/show-user/" + registry.privateId;

    return <Row className="mb-3">
        <Col>
            <Link to={link} className="text-decoration-none">
                <h5 className="text-start">{registry.title}</h5>
            </Link>
            <p className="text-muted text-truncate">
                {registry.description}
            </p>
        </Col>
    </Row>
}