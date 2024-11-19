import {useNavigate} from "react-router-dom";
import {Button, Col, Container, Row} from "react-bootstrap";
import {useTranslation} from "react-i18next";

export default function Home() {
    const navigate = useNavigate();
    const {t} = useTranslation();
    const handleStartWishlist = () => {
        navigate("/select-type");
    };

    return (
        <Container className="mt-5 d-flex justify-content-center " style={{minHeight: "80vh"}}>
            <Row className="w-100 justify-content-center">
                <Col xs={12} md={10} lg={8}>
                    <h1 className="mb-4 text-center">{t("home_page_title")}</h1>
                    <p className="text-muted text-center">
                        {t("home_page_description")}
                    </p>
                    <h4 className="mt-4">
                        {t("home_page_how_it_works")}
                    </h4>
                    <ul className="mt-3">
                        <li>
                            <strong>{t("home_page_create_strong")}</strong>
                            &nbsp;{t("home_page_create_description")}</li>
                        <li>
                            <strong>{t("home_page_mode_strong")}</strong>
                            &nbsp;{t("home_page_mode_description")}</li>
                        <li>
                            <strong>{t("home_page_share_strong")}</strong>
                            &nbsp;{t("home_page_share_description")}</li>
                    </ul>
                    <p className="text-center mt-4">
                        <strong>{t("home_page_ready_strong")}</strong>
                        &nbsp;{t("home_page_ready_description")}
                    </p>
                    <div className="text-center mt-4">
                        <Button variant="primary" size="lg" onClick={handleStartWishlist}>
                            {t("home_page_button")}
                        </Button>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}