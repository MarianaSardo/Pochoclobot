-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 18-12-2023 a las 16:15:34
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `chatbot_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `responses`
--

CREATE TABLE `responses` (
  `id_response` int(11) NOT NULL,
  `intent` varchar(50) NOT NULL,
  `response` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `responses`
--

INSERT INTO `responses` (`id_response`, `intent`, `response`) VALUES
(1, 'saludo', '¡Hola! ¿Qué genero de películas te gustaría que te muestre?'),
(2, 'despedida', '¡Nos vemos! ¡Que disfrutes tu película!'),
(3, 'agradecimiento', '¡De nada! ¡Estoy aquí para ayudar!'),
(4, 'generos', 'Puedo sugerirte películas de acción, aventura,comedia,crimen, documental, drama, familia, fantasía, historia, terror,  musicales, de misterio, romance, ciencia ficción, deportes, thriller, guerra o western.'),
(7, 'Action', 'Estas son algunas películas de acción: '),
(8, 'Adventure', 'Estas son algunas películas de aventura: '),
(9, 'Comedy', 'Estas son algunas películas cómicas: '),
(10, 'Crime', 'Estas son algunas películas de crimen: '),
(11, 'Documentary', 'Estos son algunos documentales: '),
(12, 'Drama', 'Estas son algunas películas de drama: '),
(13, 'Family', 'Estas son algunas películas para toda la familia: '),
(14, 'Fantasy', 'Estas son algunas películas de fantasía: '),
(15, 'History', 'Estas son algunas películas sobre historia: '),
(16, 'Horror', 'Estas son algunas películas de terror: '),
(17, 'Music', 'Estas son algunas películas musicales: '),
(18, 'Mystery', 'Estas son algunas películas de misterio: '),
(19, 'Romance', 'Estas son algunas películas romanticas: '),
(20, 'Sci-Fi', 'Estas son algunas películas ciencia ficción: '),
(21, 'Sport', 'Estas son algunas películas de deporte: '),
(22, 'Thriller', 'Estas son algunas películas de thriller: '),
(23, 'War', 'Estas son algunas películas de guerra: '),
(24, 'Western', 'Estas son algunas películas de western: ');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `password` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `password`) VALUES
(11, 'mari', '48690');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_generosfav`
--

CREATE TABLE `usuario_generosfav` (
  `id_usergen` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_response` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario_generosfav`
--

INSERT INTO `usuario_generosfav` (`id_usergen`, `id_usuario`, `id_response`) VALUES
(78, 11, 7),
(79, 11, 12),
(80, 11, 9),
(81, 11, 19),
(82, 11, 19),
(83, 11, 24),
(84, 11, 7);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `responses`
--
ALTER TABLE `responses`
  ADD PRIMARY KEY (`id_response`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`);

--
-- Indices de la tabla `usuario_generosfav`
--
ALTER TABLE `usuario_generosfav`
  ADD PRIMARY KEY (`id_usergen`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_response` (`id_response`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `responses`
--
ALTER TABLE `responses`
  MODIFY `id_response` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `usuario_generosfav`
--
ALTER TABLE `usuario_generosfav`
  MODIFY `id_usergen` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=85;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `usuario_generosfav`
--
ALTER TABLE `usuario_generosfav`
  ADD CONSTRAINT `usuario_generosfav_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `usuario_generosfav_ibfk_2` FOREIGN KEY (`id_response`) REFERENCES `responses` (`id_response`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
