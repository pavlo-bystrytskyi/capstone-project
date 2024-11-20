import {useTranslation} from "react-i18next";
import {Button, Container, Image, Nav, Navbar} from "react-bootstrap";
import SelectLanguage from "./SelectLanguage.tsx";
import useAuth from "../../context/UserAuth.tsx";

export default function Header() {
    const {t} = useTranslation();
    const {user, login, logout} = useAuth();
    const isLoggedIn = !!user.email;
    const handleButtonClick = () => {
        if (isLoggedIn) {
            logout();
        } else {
            login();
        }
    };

    return (
        <Navbar bg="light" expand="lg" className="mb-4 sticky-top">
            <Container fluid className="px-4">
                <Navbar.Brand>
                    <SelectLanguage/>
                </Navbar.Brand>
                <Nav className="ms-3">
                    <Nav.Item>
                        <Nav.Link href="/" className="ms-3">{t("link_home")}</Nav.Link>
                    </Nav.Item>
                    <Nav.Item>
                        <Nav.Link href="/select-type" className="ms-3">{t("link_create")}</Nav.Link>
                    </Nav.Item>
                    {isLoggedIn && (
                        <Nav.Item>
                            <Nav.Link href="/show-all" className="ms-3">{t("link_all_registries")}</Nav.Link>
                        </Nav.Item>
                    )}
                </Nav>
                <Navbar.Brand className="ms-auto d-flex flex-column align-items-start">
            <span>
                {t("hello")}, {isLoggedIn && user?.firstName ? user.firstName : t("guest")}
                {isLoggedIn && user?.picture && (
                    <Image
                        src={user.picture}
                        roundedCircle
                        className="ms-2"
                        style={{width: '30px', height: '30px'}}
                    />
                )}
            </span>
                    <Button
                        variant={isLoggedIn ? "outline-danger" : "outline-primary"}
                        onClick={handleButtonClick}
                        className="mt-2 w-100"
                    >
                        {t(isLoggedIn ? "logout" : "login")}
                    </Button>
                </Navbar.Brand>
            </Container>
        </Navbar>
    );
}
