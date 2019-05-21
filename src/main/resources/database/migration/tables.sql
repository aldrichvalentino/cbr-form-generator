-- phpMyAdmin SQL Dump
-- version 4.8.4
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 21, 2019 at 04:07 PM
-- Server version: 10.1.37-MariaDB
-- PHP Version: 7.3.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `travel`
--

-- --------------------------------------------------------

--
-- Table structure for table `controlbuttons`
--

CREATE TABLE `controlbuttons` (
  `cid` int(11) NOT NULL,
  `controlname` varchar(30) DEFAULT NULL,
  `CFormId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `formdes`
--

CREATE TABLE `formdes` (
  `formId` int(11) NOT NULL,
  `formName` varchar(30) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `forms`
--

CREATE TABLE `forms` (
  `Id` int(11) NOT NULL,
  `formdes` int(11) DEFAULT NULL,
  `formsol` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `formsol`
--

CREATE TABLE `formsol` (
  `formId` int(11) NOT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `gmembers`
--

CREATE TABLE `gmembers` (
  `id` int(11) NOT NULL,
  `groupId` int(11) DEFAULT NULL,
  `membername` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `id` int(11) NOT NULL,
  `gFormId` int(11) DEFAULT NULL,
  `idx` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `hlmembers`
--

CREATE TABLE `hlmembers` (
  `id` int(11) NOT NULL,
  `memberName` varchar(30) NOT NULL,
  `hlFormId` int(11) DEFAULT NULL,
  `idx` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `inputfields`
--

CREATE TABLE `inputfields` (
  `iid` int(11) NOT NULL,
  `ifieldname` varchar(30) DEFAULT NULL,
  `IFormId` int(11) DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `labels`
--

CREATE TABLE `labels` (
  `id` int(11) NOT NULL,
  `lFormId` int(11) NOT NULL DEFAULT '0',
  `membername` varchar(30) NOT NULL DEFAULT '0',
  `label` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `mytable`
--

CREATE TABLE `mytable` (
  `id` int(3) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `omembers`
--

CREATE TABLE `omembers` (
  `id` int(11) NOT NULL,
  `Idx` int(11) DEFAULT NULL,
  `mOrderId` int(11) DEFAULT NULL,
  `membername` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` int(11) NOT NULL,
  `oFormId` int(11) DEFAULT NULL,
  `idx` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `outputfields`
--

CREATE TABLE `outputfields` (
  `oid` int(11) NOT NULL,
  `ofieldname` varchar(30) DEFAULT NULL,
  `OFormId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `vlmembers`
--

CREATE TABLE `vlmembers` (
  `id` int(11) NOT NULL,
  `memberName` varchar(30) NOT NULL,
  `vlFormId` int(11) DEFAULT NULL,
  `idx` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `controlbuttons`
--
ALTER TABLE `controlbuttons`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `formdes`
--
ALTER TABLE `formdes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `forms`
--
ALTER TABLE `forms`
  ADD PRIMARY KEY (`Id`);

--
-- Indexes for table `formsol`
--
ALTER TABLE `formsol`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `gmembers`
--
ALTER TABLE `gmembers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `hlmembers`
--
ALTER TABLE `hlmembers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `inputfields`
--
ALTER TABLE `inputfields`
  ADD PRIMARY KEY (`iid`);

--
-- Indexes for table `labels`
--
ALTER TABLE `labels`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `mytable`
--
ALTER TABLE `mytable`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `omembers`
--
ALTER TABLE `omembers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `outputfields`
--
ALTER TABLE `outputfields`
  ADD PRIMARY KEY (`oid`);

--
-- Indexes for table `vlmembers`
--
ALTER TABLE `vlmembers`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `controlbuttons`
--
ALTER TABLE `controlbuttons`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `formdes`
--
ALTER TABLE `formdes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `forms`
--
ALTER TABLE `forms`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `formsol`
--
ALTER TABLE `formsol`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `gmembers`
--
ALTER TABLE `gmembers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `hlmembers`
--
ALTER TABLE `hlmembers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inputfields`
--
ALTER TABLE `inputfields`
  MODIFY `iid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `labels`
--
ALTER TABLE `labels`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `mytable`
--
ALTER TABLE `mytable`
  MODIFY `id` int(3) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `omembers`
--
ALTER TABLE `omembers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `outputfields`
--
ALTER TABLE `outputfields`
  MODIFY `oid` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `vlmembers`
--
ALTER TABLE `vlmembers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
