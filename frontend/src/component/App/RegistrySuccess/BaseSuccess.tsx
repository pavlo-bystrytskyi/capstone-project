import {useTranslation} from "react-i18next";
import {Button, Col, Row} from "react-bootstrap";

export default function BaseSuccess(
    {
        privateLink,
        publicLink
    }: {
        readonly privateLink: string,
        readonly publicLink: string
    }
) {
    const {t} = useTranslation();

    return (
        <Row className="w-100 justify-content-center px-3">
            <Col
                className="p-5 shadow-lg rounded bg-white"
                style={{maxWidth: '800px', width: '100%'}}
            >
                <Row className="text-center mb-4">
                    <h2>{t("registry_created")}</h2>
                </Row>
                <Row className="private-data mb-4">
                    <Col>
                        <p><strong>{t("private_link")}</strong></p>
                        <p className="text-break">{privateLink}</p>
                        <div className="d-flex align-items-center">
                            <Button
                                variant="primary"
                                className="me-2"
                                onClick={() => window.open(privateLink)}
                            >
                                {t("open_link")}
                            </Button>
                            <Button
                                variant="secondary"
                                onClick={() => navigator.clipboard.writeText(privateLink)}
                            >
                                {t("copy_link")}
                            </Button>
                        </div>
                        <p className="mt-2 text-muted">{t("private_link_description")}</p>
                    </Col>
                </Row>
                <Row className="public-data">
                    <Col>
                        <p><strong>{t("public_link")}</strong></p>
                        <p className="text-break">{publicLink}</p>
                        <div className="d-flex align-items-center">
                            <Button
                                variant="primary"
                                className="me-2"
                                onClick={() => window.open(publicLink)}
                            >
                                {t("open_link")}
                            </Button>
                            <Button
                                variant="secondary"
                                onClick={() => navigator.clipboard.writeText(publicLink)}
                            >
                                {t("copy_link")}
                            </Button>
                        </div>
                        <p className="mt-2 text-muted">{t("public_link_description")}</p>
                    </Col>
                </Row>
            </Col>
        </Row>
    )
}
